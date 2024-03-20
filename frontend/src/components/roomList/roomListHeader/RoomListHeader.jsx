import './RoomListHeader.css'
import { LoginContext } from '../../../contexts/LoginContextProvider';
import { useContext } from 'react';

const RoomListHeader = () => {

    const { logout } = useContext(LoginContext);

    return (
        <div className='header-container'>
            <div><h3>채팅</h3></div>
            <button className='' onClick={ () => logout() }>로그아웃</button>
        </div>
    );
}

export default RoomListHeader;