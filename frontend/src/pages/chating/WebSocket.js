import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export const connectWebSocket = (roomId, token, onMessageReceived, onError) => {
    const socket = new SockJS(`http://localhost:8888/stomp/ws`);

    stompClient = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
            Authorization: `Bearer ${token}`
        },
        onConnect: () => {
            console.log('Connected');
            stompClient.subscribe(`/stomp/sub/chat/${roomId}`, (message) => {
                if (message.body) {
                    const receivedMessage = JSON.parse(message.body);
                    onMessageReceived(receivedMessage);
                }
            });
        },
        onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
            onError(frame);
        },
        onWebSocketClose: (event) => {
            console.error('WebSocket closed with reason: ' + event.reason);
        },
        onWebSocketError: (event) => {
            console.error('WebSocket error: ', event);
        }
    });

    stompClient.activate();
};

export const disconnectWebSocket = () => {
    if (stompClient) {
        stompClient.deactivate();
    }
};

export const sendMessage = (roomId, message, userInfo) => {
    if (!message.trim()) {
        return; // 메시지가 공백일 경우 함수 종료
    }
    if (stompClient && stompClient.connected) {
        const chatMessage = {
            roomId: roomId,
            content: message,
            senderUserId: userInfo.userId,
            senderNickname: userInfo.nickname,
            type: 'CHAT',
            sendTime: new Date().toISOString()
        };
        stompClient.publish({ 
            destination: '/stomp/pub/send', 
            body: JSON.stringify(chatMessage) 
        });
    }
};
