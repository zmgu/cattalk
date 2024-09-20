import React, { useEffect, useState, useContext } from 'react';
import { chatRoomList } from '../../apis/chat';
import roomList from './RoomList.module.css';
import { LoginContext } from '../../contexts/LoginContextProvider';
import { useNavigate } from 'react-router-dom';
import profile_null from './../../assets/profile/profile_null.jpg';

const RoomList = () => {

    const [rooms, setRooms] = useState([]);
    const { userInfo } = useContext(LoginContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchChatRoomList = async () => {
            try {
                const response = await chatRoomList(userInfo.userId);
                const sortedRooms = response.data.sort((a, b) => new Date(b.sendTime) - new Date(a.sendTime));
                setRooms(sortedRooms);
            } catch (error) {
                console.error("채팅방을 불러오는 데 실패했습니다.", error);
            }
        };

        fetchChatRoomList();
    }, [userInfo]);

    const handleDoubleClick = async (roomName, roomId) => {
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
