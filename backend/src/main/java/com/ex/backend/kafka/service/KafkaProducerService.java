package com.ex.backend.kafka.service;

import com.ex.backend.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC = "chat";

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public void sendMessage(ChatMessage message) {
        kafkaTemplate.send(TOPIC, message.getRoomId(), message);
    }
}
