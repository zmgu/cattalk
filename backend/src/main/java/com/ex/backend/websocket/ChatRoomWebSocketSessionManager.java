package com.ex.backend.websocket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomWebSocketSessionManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, String> hashOps;

    private static final String ROOM_PREFIX = "room:";
    private static final String SESSION_PREFIX = "session:";

    @PostConstruct
    private void init() {
        this.hashOps = redisTemplate.opsForHash();
    }

    public void addUserSession(String roomId, Long userId, String sessionId) {
        hashOps.put(ROOM_PREFIX + roomId, String.valueOf(userId), sessionId);
        hashOps.put(SESSION_PREFIX + sessionId, "userId", String.valueOf(userId));
        hashOps.put(SESSION_PREFIX + sessionId, "roomId", roomId);
    }

    public void removeUserSession(String sessionId) {
        String userId = hashOps.get(SESSION_PREFIX + sessionId, "userId");
        String roomId = hashOps.get(SESSION_PREFIX + sessionId, "roomId");

        if (userId != null && roomId != null) {
            hashOps.delete(ROOM_PREFIX + roomId, userId);

            Set<String> fields = hashOps.keys(SESSION_PREFIX + sessionId);
            if (!fields.isEmpty()) {
                hashOps.delete(SESSION_PREFIX + sessionId, fields.toArray());
            }
        }
    }

    public String getRoomIdBySessionId(String sessionId) {
        return hashOps.get(SESSION_PREFIX + sessionId, "roomId");
    }

    public Long getUserIdBySessionId(String sessionId) {
        String userIdStr = hashOps.get(SESSION_PREFIX + sessionId, "userId");
        return (userIdStr != null) ? Long.parseLong(userIdStr) : null;
    }

    public List<Long> getRoomAllUserByRoomId(String roomId) {
        Map<String, String> userSessions = hashOps.entries(ROOM_PREFIX + roomId);
        return userSessions.keySet().stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}


