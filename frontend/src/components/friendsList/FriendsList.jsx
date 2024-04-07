import React, { useEffect, useState, useContext } from 'react';
import { LoginContext } from '../../contexts/LoginContextProvider';
import { friendsList } from '../../apis/auth';
import { findChatRoom, createChatRoom } from '../../apis/chat';
import Friend from './FriendsList.module.css';
import profile_null from '../../assets/profile/profile_null.jpg';
import { useNavigate } from 'react-router-dom';

const FriendsList = () => {
    const [friends, setFriends] = useState([]);
    const { userInfo } = useContext(LoginContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchFriendsList = async () => {
            try {
                const response = await friendsList(userInfo.userId);
                setFriends(response.data);
            } catch (error) {
                console.error("친구 목록을 불러오는 데 실패했습니다.", error);
            }
        };

        fetchFriendsList();
    }, [userInfo.userId]); // userInfo.userId가 변경될 때마다 fetch를 다시 실행

    const handleDoubleClick = async (friendUserId, friendNickname) => {
        try {
            const response = await findChatRoom(userInfo.userId, friendUserId);
            if (response.data.roomId) {
                // 공통 채팅방이 있다면, 해당 채팅방으로 이동
                navigate(`/chat/${response.data.roomId}`);
            } else {
                // 공통 채팅방이 없다면, 새 채팅방을 생성하고 이동
                const newRoomResponse = await createChatRoom(userInfo.userId, userInfo.nickname, friendUserId, friendNickname);
                navigate(`/chat/${newRoomResponse.data.roomId}`);
            }
        } catch (error) {
            console.error("채팅방 처리 중 에러 발생", error);
        }
    };

    return (
        <div className={Friend['list-container']}>
            {friends.map(friend => (
                <div key={friend.userId} 
                    className={Friend['container']} 
                    onDoubleClick={() => handleDoubleClick(friend.userId, friend.nickname)}>
                    <div className={Friend['image-box']}>
                        <img src={profile_null} alt='' className={Friend['image']}/>
                    </div>
                    <div className={Friend['name']}>{friend.nickname}</div>
                </div>
            ))}
        </div>
    );
}

export default FriendsList;
