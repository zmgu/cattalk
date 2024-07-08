package com.ex.backend.redis;

import com.ex.backend.chat.entity.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {

            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());

            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            messagingTemplate.convertAndSend("/stomp/sub/chat/" + roomMessage.getRoomId(), roomMessage);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
