import './Chat.css';
import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPaperclip, faPaperPlane, faArrowLeft, faMagnifyingGlass, faBars } from '@fortawesome/free-solid-svg-icons';
import { LoginContext } from '../../contexts/LoginContextProvider';
import { connectWebSocket, disconnectWebSocket, sendMessage } from './WebSocket';

const Chat = () => {
    const location = useLocation();
    const [roomDetails, setRoomDetails] = useState(location.state || {});
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const chatContainerRef = useRef(null);
    const chatInputRef = useRef(null);
    const chatMessageRef = useRef(null);
    const { userInfo } = useContext(LoginContext);

    useEffect(() => {
        if (chatMessageRef.current) {
            const observer = new MutationObserver(scrollToBottom);
            observer.observe(chatMessageRef.current, { childList: true });
            
            return () => {
                observer.disconnect();
            };
        }
    }, []);

    useEffect(() => {
        adjustChatMessageHeight();
        window.addEventListener('resize', adjustChatMessageHeight);

        const token = localStorage.getItem('Authorization');

        connectWebSocket(
            roomDetails.roomId,
            token,
            (receivedMessage) => setMessages(prevMessages => [...prevMessages, receivedMessage]),
            (error) => console.error('WebSocket error:', error)
        );

        return () => {
            window.removeEventListener('resize', adjustChatMessageHeight);
            disconnectWebSocket();
        };
    }, []);

    const handleMessageChange = (event) => {
        const textarea = event.target;
        textarea.style.height = 'auto';
        const maxHeight = 200;
        const singleLineHeight = 18;
        const actualHeight = Math.min(textarea.scrollHeight, maxHeight);
    
        if (actualHeight <= singleLineHeight * 2) {
            textarea.style.height = `${singleLineHeight}px`;
        } else {
            textarea.style.height = `${actualHeight}px`;
        }
        textarea.style.overflowY = textarea.scrollHeight > maxHeight ? 'auto' : 'hidden';
        setMessage(event.target.value);
    
        if (chatInputRef.current) {
            chatInputRef.current.style.height = 'auto';
            const newInputHeight = textarea.offsetHeight + 20;
            const previousHeight = chatInputRef.current.offsetHeight;
            chatInputRef.current.style.height = `${newInputHeight}px`;
    
            // 스크롤 위치를 유지하기 위해 chat-message의 높이 조정
            if (chatMessageRef.current) {
                const chatMessageHeight = chatMessageRef.current.offsetHeight;
                const newChatMessageHeight = chatMessageHeight - (newInputHeight - previousHeight);
                chatMessageRef.current.style.height = `${newChatMessageHeight}px`;
            }
        }
    };

    const adjustChatMessageHeight = () => {
        if (chatContainerRef.current && chatInputRef.current && chatMessageRef.current) {
            const containerHeight = chatContainerRef.current.offsetHeight;
            const inputHeight = chatInputRef.current.offsetHeight;
            const headerHeight = 70;
            const messageHeight = containerHeight - inputHeight - headerHeight;
            chatMessageRef.current.style.height = `${messageHeight}px`;
        }
    };

    const handleSendMessage = () => {
        sendMessage(roomDetails.roomId, message, userInfo);
        setMessage('');
        
        if (chatInputRef.current) {
            const inputElements = chatInputRef.current.getElementsByClassName('message-input');
            if (inputElements.length > 0) {
                inputElements[0].style.height = '18px';
            }
            chatInputRef.current.style.height = '50px';
        }
        adjustChatMessageHeight();
        scrollToBottom();
    };

    const scrollToBottom = () => {  // 스크롤 하단 이동
        if (chatMessageRef.current) {
            chatMessageRef.current.scrollTop = chatMessageRef.current.scrollHeight;
        }
    };

    const handleKeyDown = (event) => {  // 엔터키로 메시지 전송
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            handleSendMessage();
        }
    };

    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const ampm = hours >= 12 ? '오후' : '오전';
        const formattedHours = hours % 12 || 12; // 0시를 12시로
        const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;
        return `${ampm} ${formattedHours}:${formattedMinutes}`;
    };
    
    return (
        <div className='chat-container' ref={chatContainerRef}>

            {/* 상단 영역 */}
            <div className='chat-header'>
                <div className='chat-header-left'>
                    <Link to='/'><FontAwesomeIcon icon={faArrowLeft} className='chat-header-icon' /></Link>
                </div>
                <div className='chat-header-center'>
                    <div className=''>{roomDetails.roomName}</div>
                </div>
                <div className='chat-header-right'>
                    <FontAwesomeIcon icon={faMagnifyingGlass} className='chat-header-icon' />
                    <FontAwesomeIcon icon={faBars} className='chat-header-icon' />
                </div>
            </div>

            {/* 메시지 출력 */}
            <div className='chat-message' ref={chatMessageRef}>
                {messages.map((msg, index) => {
                    const isMyMessage = msg.senderUserId === userInfo.userId;
                    const showTime = 
                        index === messages.length - 1 || 
                        (messages[index + 1]?.senderUserId !== msg.senderUserId || 
                        new Date(messages[index + 1]?.sendTime).getMinutes() !== new Date(msg.sendTime).getMinutes());

                    const showNickname = 
                        index === 0 ||
                        messages[index - 1]?.senderUserId !== msg.senderUserId ||
                        new Date(messages[index - 1]?.sendTime).getMinutes() !== new Date(msg.sendTime).getMinutes();

                    return (
                        <div key={index}>
                            {!isMyMessage && showNickname && (
                                <div className="another-chat-sender">
                                    {msg.senderNickname}
                                </div>
                            )}
                            <div className={`chat-message-detail ${isMyMessage ? 'my-message' : 'another-message'}`}>
                                {isMyMessage ? (
                                    <>
                                        <div className="chat-space"></div>
                                        {showTime && (
                                            <div className={`message-time my-time`}>
                                                {formatTime(msg.sendTime)}
                                            </div>
                                        )}
                                        <div className="message my-chat">
                                            <div className='message-content'>
                                                {msg.content}
                                            </div>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div className="message another-chat">
                                            <div className='message-content'>
                                                {msg.content}
                                            </div>
                                        </div>
                                        {showTime && (
                                            <div className={`message-time another-time`}>
                                                {formatTime(msg.sendTime)}
                                            </div>
                                        )}
                                        <div className="chat-space"></div>
                                    </>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>
            
            {/* 입력 영역 */}
            <div className='chat-input' ref={chatInputRef}>
                <button className='file-attach-button'><FontAwesomeIcon icon={faPaperclip} /></button>
                <textarea
                    className='message-input'
                    value={message}
                    onChange={handleMessageChange}
                    onKeyDown={handleKeyDown}
                    rows={1}
                />
                <button className='send-message-button' onClick={handleSendMessage}><FontAwesomeIcon icon={faPaperPlane} className='chat-input-icon' /></button>
            </div>
        </div>
    );
};

export default Chat;
