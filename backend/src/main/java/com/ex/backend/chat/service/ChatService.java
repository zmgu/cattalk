package com.ex.backend.chat.service;

import com.ex.backend.chat.domain.ChatMessage;
import com.ex.backend.redis.ChatMessageRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRedis chatMessageRedis;

    public ChatMessage saveMessage(ChatMessage message) {

        chatMessageRedis.saveChatMessage(message);

        return message;
    }
}
