package com.ex.backend.kafka.service;

import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.kafka.constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(ChatMessage chatMessage) {
        kafkaTemplate.send(KafkaConstants.CHAT_TOPIC, chatMessage);
    }
}