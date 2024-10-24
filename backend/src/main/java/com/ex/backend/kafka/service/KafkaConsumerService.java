package com.ex.backend.kafka.service;

import com.ex.backend.chat.dto.ChatMessageResponseDto;
import com.ex.backend.chat.dto.ChatRoomListMessageResponseDto;
import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.chat.service.ChatRoomService;
import com.ex.backend.kafka.constants.KafkaConstants;
import com.ex.backend.websocket.ChatRoomListWebSocketSessionManager;
import com.ex.backend.websocket.ChatRoomWebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatRoomWebSocketSessionManager chatRoomSessionManager;
    private final ChatRoomListWebSocketSessionManager chatRoomListSessionManager;

    @KafkaListener(topics = KafkaConstants.CHAT_TOPIC, containerFactory = "chatKafkaListenerContainerFactory", concurrency = KafkaConstants.CHAT_CONCURRENCY)
    public void messageListener(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();

        Map<Long, Date> lastReadTimes = new HashMap<>();

        List<Long> userIds = chatRoomSessionManager.getRoomAllUserByRoomId(roomId);

        /**
         * 웹소켓 연결 중인 유저의 lastMessageReadAt 업데이트
         */
        for (Long userId : userIds) {
            Date lastMessageReadAt = chatMessage.getSendTime();
            chatRoomService.updateLastReadTimeRedis(userId, roomId, lastMessageReadAt);
            lastReadTimes.put(userId, lastMessageReadAt);
        }

        ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(chatMessage, lastReadTimes);
        messagingTemplate.convertAndSend("/stomp/sub/chat/" + roomId, chatMessageResponseDto);

        /**
         *  채팅방 목록 웹소켓에 연결 중인 유저에게 최신메시지 전송
         */
        List<Long> roomListUserIds = chatRoomListSessionManager.getRoomAllUserByRoomId(roomId);

        for (Long userId : roomListUserIds) {
            ChatRoomListMessageResponseDto roomMessage = ChatRoomListMessageResponseDto.builder()
                    .roomId(roomId)
                    .content(chatMessage.getContent())
                    .sendTime(chatMessage.getSendTime())
                    .build();

            messagingTemplate.convertAndSend("/stomp/sub/chat/" + userId, roomMessage);
        }
    }
}