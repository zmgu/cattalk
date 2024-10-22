package com.ex.backend.kafka.service;

import com.ex.backend.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(ChatMessage chatMessage) {
        kafkaTemplate.send("chat", chatMessage);
    }
}