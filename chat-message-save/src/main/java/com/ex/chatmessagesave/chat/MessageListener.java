package com.ex.chatmessagesave.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final ChatMessageRepository chatMessageRepository;

    @KafkaListener(topics = "chat", containerFactory = "chatKafkaListenerContainerFactory", concurrency = "10")
    public void messageListener(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }
}
