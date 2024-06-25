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
        const ChatRoomList = async () => {
            try {
                const response = await chatRoomList(userInfo.userId);
                setRooms(response.data);

            } catch (error) {
                console.error("채팅방을 불러오는 데 실패했습니다.", error);
            }
        };

        ChatRoomList();
    }, [userInfo]);

    const handleDoubleClick = async (roomName, roomId) => {
        navigate(`/chat/${roomId}`, { 
            state: { 
            roomId: roomId,
            roomName: roomName
            } 
        } );
    };
// =========================================================================================================
    return (
        <div className={roomList['list-container']}>
        {rooms.map(room => (
            <div key={room.roomId} 
                className={roomList['container']} 
                onDoubleClick={() => handleDoubleClick(room.roomName, room.roomId)}>
                <div className={roomList['image-box']}>
                    <img src={profile_null} alt='' className={roomList['image']}/>
                </div>
                <div className={roomList['name']}>{room.roomName}</div>
            </div>
        ))}
    </div>
    );
}

export default RoomList;