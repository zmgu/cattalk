import './Chat.css'
import React, { useState, useEffect, useRef } from 'react';

const Chat = () => {

    const [message, setMessage] = useState('');
    const chatContainerRef = useRef(null); // chat-container를 위한 ref
    const chatInputRef = useRef(null); // chat-input을 위한 ref
    const chatMessageRef = useRef(null); // chat-message를 위한 ref

    const handleMessageChange = (event) => {
        const textarea = event.target;
        textarea.style.height = 'auto'; // 높이를 자동으로 조절하기 전에 초기화
        const maxHeight = 200; // 최대 높이 설정
        textarea.style.height = `${Math.min(textarea.scrollHeight, maxHeight)}px`;
        textarea.style.overflowY = textarea.scrollHeight > maxHeight ? 'auto' : 'hidden';
        setMessage(event.target.value);
        console.log(chatInputRef.current);
        // chat-input 컨테이너의 높이 조절
        if (chatInputRef.current) {
            // chat-input 컨테이너의 높이를 textarea의 새 높이 + 패딩 등을 고려하여 조절
            chatInputRef.current.style.height = `auto`; // 먼저 auto로 설정하여 컨테이너가 내용을 수용할 수 있도록 함
            const newInputHeight = textarea.offsetHeight + 20; // 예시: textarea의 offsetHeight에 20px의 패딩 또는 여백을 추가
            chatInputRef.current.style.height = `${newInputHeight}px`;
        }
    };

    const adjustChatMessageHeight = () => {
        if (chatContainerRef.current && chatInputRef.current && chatMessageRef.current) {
            const containerHeight = chatContainerRef.current.offsetHeight; // chat-container의 전체 높이
            const inputHeight = chatInputRef.current.offsetHeight; // chat-input의 동적 높이
            const headerHeight = 70; // chat-header의 고정된 높이
            const messageHeight = containerHeight - inputHeight - headerHeight; // chat-message 영역의 새 높이 계산
            chatMessageRef.current.style.height = `${messageHeight}px`; // chat-message 영역의 높이 조정
        }
    };
    
    useEffect(() => {
        adjustChatMessageHeight();
        // 창 크기 조정에 대응하기 위한 이벤트 리스너 추가
        window.addEventListener('resize', adjustChatMessageHeight);
        return () => {
            window.removeEventListener('resize', adjustChatMessageHeight);
        };
    }, []); // 의존성 배열에 관련 상태를 추가하여 높이 조절 로직이 반응하도록 설정
    
    return (
        <div className='chat-container'  ref={chatContainerRef}>
            <div className='chat-header'></div>
            <div className='chat-message' ref={chatMessageRef}></div>
            <div className='chat-input' ref={chatInputRef}>
            <button className='file-attach-button'>📎</button>
                    <textarea
                        placeholder='메시지 입력...'
                        value={message}
                        onChange={handleMessageChange}
                        rows={1}
                    ></textarea>
                    <button className='send-message-button'>전송</button>
            </div>
        </div>
    );
};

export default Chat;