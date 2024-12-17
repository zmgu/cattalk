import React, { useEffect, useState, useContext } from 'react';
import ReactDOM from 'react-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { LoginContext } from '../../contexts/LoginContextProvider';
import { friendsList } from '../../apis/auth';
import { createGroupChatRoom } from '../../apis/chat';
import './Modal.css';

const Modal = ({ onClose }) => {
    const [friends, setFriends] = useState([]);
    const [selectedFriends, setSelectedFriends] = useState([]);
    const [chatRoomName, setChatRoomName] = useState('');
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
    }, [userInfo.userId]);

    const toggleFriendSelection = (friendUserId) => {
        setSelectedFriends((prevSelected) => 
            prevSelected.includes(friendUserId)
                ? prevSelected.filter((id) => id !== friendUserId)
                : [...prevSelected, friendUserId]
        );
    };

    const handleSubmit = async () => {
        try {
            // 본인 아이디를 포함한 유저 아이디 리스트 생성
            const userIds = [userInfo.userId, ...selectedFriends];

            // 그룹 채팅방 생성 API 호출
            const response = await createGroupChatRoom(chatRoomName, userIds);
            
            // 생성된 채팅방으로 이동 (roomId를 response로부터 얻음)
            const roomId = response.data;
            console.log('채팅방 생성 성공:', roomId);
            
            onClose(); // 모달 닫기
        } catch (error) {
            console.error('그룹 채팅방 생성 중 오류 발생:', error);
        }
    };

    return ReactDOM.createPortal(
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div>
                    <button onClick={onClose} className="x-mark">
                        <FontAwesomeIcon icon={faXmark} />
                    </button>
                </div>

                <h2>채팅방 생성</h2>
                <div>
                    <label>채팅방 이름:</label>
                    <input 
                        type="text" 
                        placeholder="채팅방 이름 입력" 
                        value={chatRoomName}
                        onChange={(e) => setChatRoomName(e.target.value)}
                    />
                </div>

                <div>
                    <h3>친구 리스트</h3>
                    <div className="friends-list">
                        {friends.map((friend) => (
                            <div key={friend.userId} className="friend-item">
                                <input
                                    type="checkbox"
                                    id={`friend-${friend.userId}`}
                                    checked={selectedFriends.includes(friend.userId)}
                                    onChange={() => toggleFriendSelection(friend.userId)}
                                />
                                <label htmlFor={`friend-${friend.userId}`}>
                                    {friend.nickname}
                                </label>
                            </div>
                        ))}
                    </div>
                </div>

                <div>
                    <h3>체크한 사람</h3>
                    <ul>
                        {selectedFriends.map((friendId) => {
                            const friend = friends.find((f) => f.userId === friendId);
                            return friend ? <li key={friendId}>{friend.nickname}</li> : null;
                        })}
                    </ul>
                </div>

                <div>
                    <button type="submit" onClick={handleSubmit}>생성</button>
                </div>
            </div>
        </div>,
        document.getElementById('modal-root')
    );
};

export default Modal;
