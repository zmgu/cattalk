package com.ex.backend.kafka.service;

import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.kafka.KafkaConsumerConfig;
import com.ex.backend.kafka.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final Map<String, ConcurrentMessageListenerContainer<String, ChatMessage>> containers = new HashMap<>();
    private final Logger logger = Logger.getLogger(KafkaConsumerService.class.getName());
    private final KafkaUtil kafkaUtil;

    public synchronized void startListenerForRoom(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();
        String groupId = kafkaUtil.groupIdKey(chatMessage);
        if (containers.containsKey(roomId)) {
            logger.info("컨테이너 실행중");
            // 이미 해당 roomId에 대한 리스너가 실행 중이라면, 추가로 실행하지 않음
            return;
        }

        MessageListener<String, ChatMessage> listener = record -> {
            logger.info("메시지 수신 완료");
            messagingTemplate.convertAndSend("/stomp/sub/chat/" + roomId, record.value());
        };

        ConcurrentMessageListenerContainer<String, ChatMessage> container =
                kafkaConsumerConfig.createContainer(roomId, groupId,listener);

        container.start(); // 컨테이너 시작
        logger.info("컨테이너 시작");
        containers.put(roomId, container); // 컨테이너를 저장하여 관리
    }

    public synchronized void stopListenerForRoom(String roomId) {
        ConcurrentMessageListenerContainer<String, ChatMessage> container = containers.remove(roomId);
        if (container != null) {
            container.stop(); // 컨테이너 중지
        }
    }
}