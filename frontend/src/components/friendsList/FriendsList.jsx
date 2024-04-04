import React, { useEffect, useState } from 'react';
import { friendsList } from '../../apis/auth'
import profile from './myProfile/MyProfile.module.css';
import profile_null from '../../assets/profile/profile_null.jpg';

const FriendsList = () => {
    const [friends, setFriends] = useState([]);

    useEffect(() => {
        const fetchFriendsList = async () => {
            try {
                const response = await friendsList();
                setFriends(response.data);
            } catch (error) {
                console.error("친구 목록을 불러오는 데 실패했습니다.", error);
            }
        };

        fetchFriendsList();
    }, []);

    return (
        <div className={profile['list-container']}>
            {friends.map(friend => (
                <div key={friend.id} className={profile['container']}>
                    <div className={profile['image-box']}>
                        <img src={profile_null} alt='' className={profile['image']}/>
                    </div>
                    <div className={profile['name']}>{friend.nickname}</div>
                </div>
            ))}
        </div>
    );
}

export default FriendsList;
