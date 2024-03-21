import './RoomListHeader.css'
import { LoginContext } from '../../../contexts/LoginContextProvider';
import { useContext, useState } from 'react'; // useState를 추가합니다.
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons';

const RoomListHeader = () => {
    const { logout } = useContext(LoginContext);
    const [showList, setShowList] = useState(false); // 리스트 표시 상태를 관리합니다.

    const toggleList = () => {
        setShowList(!showList); // 현재 상태의 반대값으로 설정합니다.
    }

    return (
        <div className='header-container'>
            <div className='header-title'>
                <h3>채팅</h3>
            </div>
            <div className='header-btn-box'>
                <button className='' onClick={ () => logout() }>로그아웃</button>
                <button className='roomlist-header-btn' onClick={toggleList}>
                    <FontAwesomeIcon icon={faGear} className='roomlist-header-icon' />
                </button>
                {/* showList 상태에 따라 리스트를 표시하거나 숨깁니다. */}
                {showList && (
                    <div className='roomlist-options'>
                        {/* 여기에 리스트 아이템을 추가합니다. 예시로 몇 가지를 넣어두었습니다. */}
                        <button>옵션 1</button>
                        <button>옵션 2</button>
                        <button>옵션 3</button>
                    </div>
                )}
            </div>

        </div>

    );
}

export default RoomListHeader;
