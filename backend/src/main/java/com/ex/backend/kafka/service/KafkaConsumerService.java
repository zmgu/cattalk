package com.ex.backend.kafka.service;

import com.ex.backend.chat.dto.ChatMessageResponseDto;
import com.ex.backend.chat.dto.ChatRoomListMessageResponseDto;
import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.chat.service.ChatRoomService;
import com.ex.backend.kafka.KafkaConsumerConfig;
import com.ex.backend.kafka.KafkaUtil;
import com.ex.backend.websocket.ChatRoomListWebSocketSessionManager;
import com.ex.backend.websocket.ChatRoomWebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ChatRoomWebSocketSessionManager chatRoomSessionManager;
    private final ChatRoomListWebSocketSessionManager chatRoomListSessionManager;
    private final Map<String, ConcurrentMessageListenerContainer<String, ChatMessage>> containers = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger(KafkaConsumerService.class.getName());
    private final KafkaUtil kafkaUtil;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;


    public void startListenerForRoom(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();
        String groupId = kafkaUtil.groupIdKey(chatMessage.getRoomId());

        // 이미 해당 roomId에 대한 리스너가 실행 중이라면, 추가로 실행하지 않음
        if (containers.containsKey(roomId)) {
            logger.info("컨테이너 실행 중");
            return;
        }

        MessageListener<String, ChatMessage> listener = record -> {
            logger.info("메시지 수신 완료");
            Map<Long, Date> lastReadTimes = new HashMap<>();

            List<Long> userIds = chatRoomSessionManager.getRoomAllUserByRoomId(roomId);

            // 웹소켓 연결 중인 유저의 lastMessageReadAt 업데이트
            for (Long userId : userIds) {
                Date lastMessageReadAt = record.value().getSendTime();
                chatRoomService.updateLastReadTimeRedis(userId, roomId, lastMessageReadAt);
                lastReadTimes.put(userId, lastMessageReadAt);

            }

            ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(record.value(), lastReadTimes);
            messagingTemplate.convertAndSend("/stomp/sub/chat/" + roomId, chatMessageResponseDto);

            /*
            *  채팅방 목록 웹소켓에 연결 중인 유저에게 최신메시지 전송
            * */
            List<Long> roomListUserIds = chatRoomListSessionManager.getRoomAllUserByRoomId(roomId);

            for (Long userId : roomListUserIds) {
                ChatRoomListMessageResponseDto roomMessage = ChatRoomListMessageResponseDto.builder()
                        .roomId(roomId)
                        .content(record.value().getContent())
                        .sendTime(record.value().getSendTime())
                        .build();
                roomMessage.setRoomId(roomId);

                messagingTemplate.convertAndSend("/stomp/sub/chat/" + userId, roomMessage);
            }

        };

        ConcurrentMessageListenerContainer<String, ChatMessage> container =
                kafkaConsumerConfig.createContainer(roomId, groupId, listener);

        container.start();
        containers.put(roomId, container);
    }

    public synchronized void stopListenerForRoom(String roomId) {
        ConcurrentMessageListenerContainer<String, ChatMessage> container = containers.remove(roomId);
        if (container != null) {
            container.stop(); // 컨테이너 중지
        }
    }
}