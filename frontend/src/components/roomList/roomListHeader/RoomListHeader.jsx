import './RoomListHeader.css'
import { LoginContext } from '../../../contexts/LoginContextProvider';
import { useContext, useState } from 'react'; // useState를 추가합니다.
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons';

const RoomListHeader = () => {
    const { logout } = useContext(LoginContext);
    const [showList, setShowList] = useState(false);
    
    const toggleList = () => {
        setShowList(!showList);
    }

    return (
        <div className='header-container'>
            <div className='header-title'>
                <div className='roomlist-logo'></div>
            </div>
            <div className='header-btn-box'>
                <button className='roomlist-header-btn' onClick={toggleList}>
                    <FontAwesomeIcon icon={faGear} className='roomlist-header-icon' />
                </button>
                

                <div className={`roomlist-options ${showList ? 'show' : ''}`}>
                    <button>최신 메시지 순</button>
                    <button>안 읽은 메시지 순</button>
                    <button>편집</button>
                    <button className='' onClick={ () => logout() }>로그아웃</button>
                </div>
                
            </div>

        </div>

    );
}

export default RoomListHeader;
