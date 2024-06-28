import './Chat.css';
import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { faPaperclip, faPaperPlane, faArrowLeft, faMagnifyingGlass, faBars } from '@fortawesome/free-solid-svg-icons';
import { LoginContext } from '../../contexts/LoginContextProvider';

const Chat = () => {
    const location = useLocation();
    const [roomDetails, setRoomDetails] = useState(location.state || {});
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const chatContainerRef = useRef(null);
    const chatInputRef = useRef(null);
    const chatMessageRef = useRef(null);
    const stompClient = useRef(null);
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

        const connect = () => {
            const token = localStorage.getItem('Authorization');
            const socket = new SockJS(`http://localhost:8888/stomp/ws`);

            stompClient.current = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    Authorization: `Bearer ${token}`
                },
                onConnect: () => {
                    console.log('Connected');
                    stompClient.current.subscribe(`/stomp/sub/chat/${roomDetails.roomId}`, (message) => {
                        if (message.body) {
                            const receivedMessage = JSON.parse(message.body);
                            setMessages(prevMessages => [...prevMessages, receivedMessage]);
                        }
                    });
                },
                onStompError: (frame) => {
                    console.error('Broker reported error: ' + frame.headers['message']);
                    console.error('Additional details: ' + frame.body);
                },
                onWebSocketClose: (event) => {
                    console.error('WebSocket closed with reason: ' + event.reason);
                },
                onWebSocketError: (event) => {
                    console.error('WebSocket error: ', event);
                }
            });

            stompClient.current.activate();
        };

        connect();

        return () => {
            window.removeEventListener('resize', adjustChatMessageHeight);
            if (stompClient.current) {
                stompClient.current.deactivate();
            }
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

    const sendMessage = () => {
        if (!message.trim()) {
            return; // 메시지가 공백일 경우 함수 종료
        }
        if (stompClient.current && stompClient.current.connected) {
            const chatMessage = {
                roomId: roomDetails.roomId,
                content: message,
                senderUserId: userInfo.userId,
                senderNickname: userInfo.nickname,
                type: 'CHAT',
                sendTime: new Date().toISOString()
            };
            stompClient.current.publish({ 
                destination: '/stomp/pub/send', 
                body: JSON.stringify(chatMessage) 
            });
            setMessage('');
            
            if (chatInputRef.current) {
                const inputElements = chatInputRef.current.getElementsByClassName('message-input');
                if (inputElements.length > 0) {
                    inputElements[0].style.height = '18px';  // textarea의 초기 높이 설정
                }
                chatInputRef.current.style.height = '50px';  // chat-input의 초기 높이 설정
            }
            adjustChatMessageHeight();
            scrollToBottom(); // 메시지를 보낸 후 스크롤을 맨 아래로 이동
        }
    };

    const scrollToBottom = () => {
        if (chatMessageRef.current) {
            chatMessageRef.current.scrollTop = chatMessageRef.current.scrollHeight;
        }
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
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
            <div className='chat-message' ref={chatMessageRef}>
                {messages.map((msg, index) => {
                    const isMyMessage = msg.senderUserId === userInfo.userId;

                    const showTime = 
                        index === messages.length - 1 || 
                        (messages[index + 1]?.senderUserId !== msg.senderUserId || 
                        new Date(messages[index + 1]?.sendTime).getMinutes() !== new Date(msg.sendTime).getMinutes());

                    return (
                        <div key={index}>
                            {!isMyMessage && (
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

            <div className='chat-input' ref={chatInputRef}>
                <button className='file-attach-button'><FontAwesomeIcon icon={faPaperclip} /></button>
                <textarea
                    className='message-input'
                    value={message}
                    onChange={handleMessageChange}
                    onKeyDown={handleKeyDown}
                    rows={1}
                />
                <button className='send-message-button' onClick={sendMessage}><FontAwesomeIcon icon={faPaperPlane} className='chat-input-icon' /></button>
            </div>
        </div>
    );
};

export default Chat;
