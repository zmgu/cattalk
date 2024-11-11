package com.ex.backend.websocket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatRoomListWebSocketSessionManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, String> hashOps;

    private static final String ROOM_LIST_PREFIX = "roomList:";

    @PostConstruct
    private void init() {
        this.hashOps = redisTemplate.opsForHash();
    }

    public boolean sessionExists(String sessionId) {
        return Objects.requireNonNull(redisTemplate.keys(ROOM_LIST_PREFIX + "*")).stream()
                .anyMatch(roomKey -> hashOps.entries(roomKey).containsValue(sessionId));
    }

    public void addUserSession(String roomId, Long userId, String sessionId) {
        hashOps.put(ROOM_LIST_PREFIX + roomId, String.valueOf(userId), sessionId);
    }

    public void removeUserSession(String sessionId) {
        redisTemplate.keys(ROOM_LIST_PREFIX + "*").forEach(roomKey -> {
            hashOps.entries(roomKey).forEach((userId, userSessionId) -> {
                if (sessionId.equals(userSessionId)) {
                    hashOps.delete(roomKey, userId);
                }
            });
        });
    }

    public List<Long> getRoomAllUserByRoomId(String roomId) {
        Map<String, String> userSessions = hashOps.entries(ROOM_LIST_PREFIX + roomId);
        return userSessions.keySet().stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public List<String> getRoomIdsBySessionId(String sessionId) {
        return redisTemplate.keys(ROOM_LIST_PREFIX + "*").stream()
                .filter(roomKey -> hashOps.entries(roomKey).containsValue(sessionId))
                .map(roomKey -> roomKey.replace(ROOM_LIST_PREFIX, ""))
                .collect(Collectors.toList());
    }
}



