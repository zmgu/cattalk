package com.ex.backend.kafka.service;

import com.ex.backend.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "chat", groupId = "chat")
    public void listen(ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/stomp/sub/chat/" + chatMessage.getRoomId(), chatMessage);
    }
}
