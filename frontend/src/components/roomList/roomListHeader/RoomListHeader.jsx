import room from './RoomListHeader.module.css';
import { useContext, useState, useEffect, useRef } from 'react';
import { LoginContext } from '../../../contexts/LoginContextProvider';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear, faCommentDots, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { ModalContext } from '../../../contexts/ModalContext';


const RoomListHeader = () => {
    const { logout } = useContext(LoginContext);
    const [showList, setShowList] = useState(false);
    const menuRef = useRef();
    const { openModal } = useContext(ModalContext);

    // 메뉴 외부 클릭 감지
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setShowList(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const toggleList = () => {
        setShowList(!showList);
    };

    return (
        <div className={room['container']} ref={menuRef}>
            <div className={room['title']}>
                <div className={room['logo']}></div>
            </div>
            <div className={room['btn-box']}>
                <button className={room['btn']}>
                    <FontAwesomeIcon icon={faMagnifyingGlass} className={room['icon']} />
                </button>

                <button className={room['btn']} onClick={openModal}>
                    <FontAwesomeIcon icon={faCommentDots} className={room['icon']} />
                </button>
    
                <button id="menuToggle" className={room['btn']} onClick={toggleList}>
                    <FontAwesomeIcon icon={faGear} className={room['icon']} />
                </button>
                
                <div className={`${room['options']} ${showList ? room['show'] : ''}`}>
                    <button>최신 메시지 순</button>
                    <button>안 읽은 메시지 순</button>
                    <button>편집</button>
                    <button onClick={() => logout()}>로그아웃</button>
                </div>
            </div>
        </div>
    );
}

export default RoomListHeader;
