package com.ex.backend.chat.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatRedis {

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    private String generateKey(Long userId, String roomId) {
        return "userIdAndRoomId:" + userId + roomId;
    }

    // 유저 데이터 저장 메서드
    public void saveUserChatInfo(Long userId, String roomId, Date sendTime) {
        String key = generateKey(userId, roomId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastMessageReadAt = sdf.format(sendTime);

        hashOperations.put(key, "roomId", roomId);
        hashOperations.put(key, "lastMessageReadAt", lastMessageReadAt);
    }

    public void deleteUserChatInfo(Long userId,  String roomId) {
        String key = generateKey(userId, roomId);
        redisTemplate.delete(key);
    }

    // 유저 데이터 조회 메서드
    public String getUserChatInfo(Long userId,  String roomId, String field) {
        String key = generateKey(userId, roomId);
        return hashOperations.get(key, field);
    }
}
