package com.ex.backend.kafka.service;

import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.kafka.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final KafkaUtil kafkaUtil;

    public void sendMessage(ChatMessage chatMessage) {
        String key = kafkaUtil.groupIdKey(chatMessage);
        kafkaTemplate.send(chatMessage.getRoomId(), key, chatMessage);
    }
}