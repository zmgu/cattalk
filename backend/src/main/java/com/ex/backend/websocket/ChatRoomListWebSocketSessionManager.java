package com.ex.backend.websocket;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChatRoomListWebSocketSessionManager {

    private final Map<String, Map<Long, String>> roomIdKeyMap = new ConcurrentHashMap<>();

    public boolean sessionExists(String sessionId) {
        return roomIdKeyMap.values().stream()
                .anyMatch(userSessions -> userSessions.containsValue(sessionId));
    }

    public void addUserSession(String roomId, Long userId, String sessionId) {
        roomIdKeyMap.putIfAbsent(roomId, new ConcurrentHashMap<>());
        roomIdKeyMap.get(roomId).put(userId, sessionId);
    }

    public void removeUserSession(String sessionId) {
        roomIdKeyMap.values().forEach(userSessions ->
                userSessions.entrySet().removeIf(entry -> sessionId.equals(entry.getValue()))
        );
    }

    public List<Long> getRoomAllUserByRoomId(String roomId) {
        Map<Long, String> userSessions = roomIdKeyMap.get(roomId);
        if (userSessions != null) {
            return new ArrayList<>(userSessions.keySet());
        }
        return Collections.emptyList();
    }


    // 세션 아이디로 채팅방 아이디들을 조회하는 메서드
    public List<String> getRoomIdsBySessionId(String sessionId) {
        return roomIdKeyMap.entrySet().stream()
                .filter(entry -> entry.getValue().containsValue(sessionId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 채팅방 아이디로 세션 아이디들을 찾는 메서드
    public List<String> getSessionIdsByRoomId(String roomId) {
        Map<Long, String> userSessions = roomIdKeyMap.get(roomId);
        if (userSessions != null) {
            return new ArrayList<>(userSessions.values());
        }
        return Collections.emptyList();
    }

}
