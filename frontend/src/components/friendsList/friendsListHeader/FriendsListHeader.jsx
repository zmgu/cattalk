import React from 'react';
import friends from './FriendsListHeader.module.css'
import { Link } from 'react-router-dom'
import { useContext, useState, useEffect, useRef } from 'react';
import { LoginContext } from '../../../contexts/LoginContextProvider';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear, faCommentDots, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';

const FriendsListHeader = () => {
    const { logout } = useContext(LoginContext);
    const [showList, setShowList] = useState(false);
    const menuRef = useRef();

    // 메뉴 외부 클릭 감지
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setShowList(false); // 메뉴 외부 클릭 시 메뉴 닫기
            }
        };

        // 문서 전체에 클릭 이벤트 리스너 추가
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const toggleList = () => {
        setShowList(!showList);
    }

    return (
        <div className={friends['container']} ref={menuRef}>
            <div className={friends['title']}>
                <div className={friends['logo']}></div>
            </div>
            <div className={friends['btn-box']}>
                <button className={friends['btn']}>
                    <FontAwesomeIcon icon={faMagnifyingGlass} className={friends['icon']} />
                </button>
                <button className={friends['btn']}>
                    <FontAwesomeIcon icon={faCommentDots} className={friends['icon']} />
                </button>
                <div className={`${friends['options']} ${showList ? friends['show'] : ''}`}>
                    <button>채팅방 만들기</button>
                </div>

                <button id="menuToggle" className={friends['btn']} onClick={toggleList}>
                    <FontAwesomeIcon icon={faGear} className={friends['icon']} />
                </button>
                
                <div className={`${friends['options']} ${showList ? friends['show'] : ''}`}>
                    <Link to='/chat'>임시 채팅방</Link>
                    <button>최신 메시지 순</button>
                    <button>안 읽은 메시지 순</button>
                    <button>편집</button>
                    <button onClick={() => logout()}>로그아웃</button>
                </div>
            </div>
        </div>
    );
}

export default FriendsListHeader;
