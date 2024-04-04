import React, { useContext } from 'react';
import { LoginContext } from '../../../contexts/LoginContextProvider';
import profile from './MyProfile.module.css';
import profile_null from './../../../assets/profile/profile_null.jpg'

const MyProfile = () => {
    const { userInfo } = useContext(LoginContext);

    return (
        <div className={profile['container']}>
            <div className={profile['image-box']}>
                <img src={profile_null} alt=''  className={profile['image']}/>
            </div>
            <div className={profile['name']}>{userInfo.nickname}</div>
        </div>
    );
}

export default MyProfile;
