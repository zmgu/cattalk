import React, { useEffect, useState, useContext } from 'react';
import { chatRoomList } from '../../apis/chat';
import roomList from './RoomList.module.css';
import { LoginContext } from '../../contexts/LoginContextProvider';
import { useNavigate } from 'react-router-dom';
import profile_null from './../../assets/profile/profile_null.jpg';
import { connectWebSocket, disconnectWebSocket } from './ChatRoomListWebSocket';

const RoomList = () => {

    const [rooms, setRooms] = useState([]);
    const { userInfo } = useContext(LoginContext);
    const navigate = useNavigate();
    let [roomIds, setRoomIds] = useState([]);


    useEffect(() => {

        if (userInfo) { 

            fetchChatRoomList();

        }

    }, [userInfo]);

    useEffect(() => {
        if(roomIds != '' && roomIds != null) {
            const token = localStorage.getItem('Authorization');
            connectWebSocket(userInfo.userId, roomIds, token, handleWebSocketMessage);
            console.log('RoomList 웹소켓 연결 완료');
        }
    }, [roomIds])

    const fetchChatRoomList = async () => {
        try {
            let response = await chatRoomList(userInfo.userId);
            let sortedRooms = response.data.sort((a, b) => new Date(b.sendTime) - new Date(a.sendTime));
            setRooms(sortedRooms);
            console.log(`response.data : ${JSON.stringify(response.data)}`)
            
            // roomId 리스트 추출
            roomIds = setRoomIds(response.data.map(room => room.roomId));

        } catch (error) {
            console.error("채팅방을 불러오는 데 실패했습니다.", error);
        }
    };

    const handleWebSocketMessage = (receivedMessage) => {
        setRooms(prevRooms => 
            prevRooms.map(room => {
                if (room.roomId === receivedMessage.roomId) {
                    console.log(`receivedMessage.sendTime : ${receivedMessage.sendTime}`);
                    return {
                        ...room,
                        content: receivedMessage.content,
                        sendTime: receivedMessage.sendTime,
                        unReadCnt: room.unReadCnt + 1
                    };
                }
                return room;
            })
        );
    };

    const handleDoubleClick = async (roomName, roomId) => {
        disconnectWebSocket();
        navigate(`/chat/${roomId}`, { 
            state: { 
                roomId: roomId,
                roomName: roomName
            } 
        });
    };

    const formatSendTime = (sendTime) => {
        const date = new Date(sendTime);
        const now = new Date();
        const diffTime = Math.abs(now - date);
        const diffMinutes = Math.floor(diffTime / (1000 * 60));
    
        const isToday = (date.getDate() === now.getDate() &&
                         date.getMonth() === now.getMonth() &&
                         date.getFullYear() === now.getFullYear());
    
        const isYesterday = (new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1).getDate() === date.getDate() &&
                             date.getMonth() === now.getMonth() &&
                             date.getFullYear() === now.getFullYear());
    
        const formatAMPM = (hours) => {
            const period = hours < 12 ? '오전' : '오후';
            const adjustedHours = hours % 12 || 12; // 12시간 형식으로 변환, 0시 -> 12시로 변환
            return { period, adjustedHours };
        };
    
        if (diffMinutes < 1) {
            return '방금';
        } else if (isToday) {
            const { period, adjustedHours } = formatAMPM(date.getHours());
            return `${period} ${adjustedHours}:${String(date.getMinutes()).padStart(2, '0')}`; // 0 제거된 시간
        } else if (isYesterday) {
            return '어제';
        } else if (date.getFullYear() === now.getFullYear()) {
            return `${date.getMonth() + 1}월 ${date.getDate()}일`;
        } else {
            return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
        }
    };

    return (
        <div className={roomList['list-container']}>
            {rooms.map(room => (
                <div key={room.roomId} 
                    className={roomList['container']} 
                    onDoubleClick={() => handleDoubleClick(room.roomName, room.roomId)}>
                    <div className={roomList['image-box']}>
                        <img src={profile_null} alt='' className={roomList['image']}/>
                    </div>
                    <div className={roomList['name-message']}>
                        <div className={roomList['name']}>{room.roomName}</div>
                        <div className={roomList['message']}>{room.content ? room.content : '메시지가 없습니다.'}</div>
                    </div>
                    <div className={roomList['sendtime-unread']}>
                        <div className={roomList['sendtime']}>{room.sendTime ? formatSendTime(room.sendTime) : ''}</div>
                            {room.unReadCnt > 0 && (
                                <div className={roomList['unread']}>
                                    <div className={roomList['red-box']}>
                                        {room.unReadCnt}
                                    </div>
                                </div>
                            )}
                        </div>
                </div>
            ))}
        </div>
    );
}

export default RoomList;
