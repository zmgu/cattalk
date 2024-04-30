package com.ex.backend.redis;

import com.ex.backend.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageRedis {

    private final RedisTemplate redisTemplate;

    public void saveChatMessage(ChatMessage chatMessage) {
        String key = "chatMessage:" + chatMessage.getRoomId();
        redisTemplate.opsForList().rightPush(key, chatMessage);
    }

}
