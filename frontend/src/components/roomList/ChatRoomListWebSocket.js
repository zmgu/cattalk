import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export const connectWebSocket = (userId, roomIds, token, onMessageReceived, onError) => {
    const socket = new SockJS(`http://localhost:8888/stomp/ws`);

    stompClient = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
            'Authorization': token,
            'Type' : 'chatRoomList',
            'RoomIds' : roomIds
        },
        onConnect: () => {
            stompClient.subscribe(`/stomp/sub/chat/${userId}`, (message) => {
                const receivedMessage = JSON.parse(message.body);
                onMessageReceived(receivedMessage);
                console.log(`message.body : ${message.body}`)
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