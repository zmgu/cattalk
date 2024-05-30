import './Chat.css';
import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { faPaperclip, faPaperPlane, faArrowLeft, faMagnifyingGlass, faBars } from '@fortawesome/free-solid-svg-icons';

import { LoginContext } from '../../contexts/LoginContextProvider';

const Chat = () => {
    console.log('chat')

    const location = useLocation();
    const [roomDetails, setRoomDetails] = useState(location.state || {});
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const chatContainerRef = useRef(null);
    const chatInputRef = useRef(null);
    const chatMessageRef = useRef(null);
    const stompClient = useRef(null);
    const { userInfo } = useContext(LoginContext);
    console.log(`${userInfo.nickname}`)

    useEffect(() => {
        console.log(`useEffect 실행중`);

        const accessToken = localStorage.getItem('Authorization');
        console.log(`accessToken : ${accessToken}`);

        adjustChatMessageHeight();
        window.addEventListener('resize', adjustChatMessageHeight);

        try {
            const socket = new SockJS('/ws');
            stompClient.current = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    'Authorization' : accessToken
                },
                onConnect: () => {
                    stompClient.current.subscribe('/sub/public', (message) => {
                        if (message.body) {
                            const receivedMessage = JSON.parse(message.body);
                            setMessages(prevMessages => [...prevMessages, receivedMessage]);
                        }
                    });
                }
            });

            stompClient.current.activate();
        } catch(error) {
            console.error(`웹소켓 연결 에러`);
        }

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
            chatInputRef.current.style.height = `${newInputHeight}px`;
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
        if (stompClient.current && stompClient.current.connected) {
            const chatMessage = {
                roomId: roomDetails.roomId,
                content: message,
                senderUserId: userInfo.userId,
                type: 'CHAT'
            };
            stompClient.current.publish({ destination: '/pub/chat.sendMessage', body: JSON.stringify(chatMessage) });
            setMessage('');
        }
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
                {messages.map((msg, index) => (
                    <div key={index} className='message'>
                        <div className='message-sender'>{msg.senderUserId}</div>
                        <div className='message-content'>{msg.content}</div>
                    </div>
                ))}
            </div>
            <div className='chat-input' ref={chatInputRef}>
                <button className='file-attach-button'><FontAwesomeIcon icon={faPaperclip} /></button>
                <textarea
                    className='message-input'
                    value={message}
                    onChange={handleMessageChange}
                    rows={1}
                />
                <button className='send-message-button' onClick={sendMessage}><FontAwesomeIcon icon={faPaperPlane} className='chat-input-icon' /></button>
            </div>
        </div>
    );
};

export default Chat;