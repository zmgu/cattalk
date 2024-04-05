import React, { useEffect, useState, useContext } from 'react';
import { LoginContext } from '../../contexts/LoginContextProvider';
import { friendsList } from '../../apis/auth'
import Friend from './FriendsList.module.css'
import profile_null from '../../assets/profile/profile_null.jpg';
import { createChatRoom } from '../../apis/chat';

const FriendsList = () => {
    const [friends, setFriends] = useState([]);
    const { userInfo } = useContext(LoginContext);


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
    }, []);

    return (
        <div className={Friend['list-container']}>
            {friends.map(friend => (
                <div key={friend.userId} 
                    className={Friend['container']} 
                    onDoubleClick={() => createChatRoom(userInfo.userId, friend.userId, friend.nickname)}>
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
