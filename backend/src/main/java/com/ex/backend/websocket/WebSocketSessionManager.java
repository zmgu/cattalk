package com.ex.backend.websocket;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    // roomId 키로 userId와 sessionId를 매핑하여 웹소켓에 연결 중인 유저를 저장하는 맵
    private final Map<String, Map<Long, String>> roomIdKeyMap = new ConcurrentHashMap<>();

    // sessionId 키로 userId와 roomId를 매핑하는 맵
    private final Map<String, Map<Long, String>> sessionIdKeyMap = new ConcurrentHashMap<>();

    // 웹소켓 연결 중인 유저 추가
    public void addUserSession(String roomId, Long userId, String sessionId) {
        roomIdKeyMap
                .computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(userId, sessionId);

        Map<Long, String> userRoomMap = new ConcurrentHashMap<>();
        userRoomMap.put(userId, roomId);
        sessionIdKeyMap.put(sessionId, userRoomMap);
    }

    // 웹소켓 연결 해제한 유저 제거
    public void removeUserSession(String sessionId) {
        Map<Long, String> userRoomMap = sessionIdKeyMap.get(sessionId);
        if (userRoomMap != null) {
            Long userId = userRoomMap.keySet().iterator().next();
            String roomId = userRoomMap.get(userId);

            Map<Long, String> userSessions = roomIdKeyMap.get(roomId);
            if (userSessions != null) {
                userSessions.remove(userId);
                if (userSessions.isEmpty()) {
                    roomIdKeyMap.remove(roomId);
                }
            }
            sessionIdKeyMap.remove(sessionId); // sessionId와 userId, roomId 매핑 제거
        }
    }

    public String getRoomIdBySessionId(String sessionId) {
        Map<Long, String> userRoomMap = sessionIdKeyMap.get(sessionId);
        return (userRoomMap != null) ? userRoomMap.values().iterator().next() : null;
    }

    // sessionId를 사용하여 userId 반환
    public Long getUserIdBySessionId(String sessionId) {
        Map<Long, String> userRoomMap = sessionIdKeyMap.get(sessionId);
        if (userRoomMap != null && !userRoomMap.isEmpty()) {
            return userRoomMap.keySet().iterator().next();
        }
        return null;
    }

    public List<Long> getRoomAllUserByRoomId(String roomId) {
        Map<Long, String> userSessions = roomIdKeyMap.get(roomId);
        if (userSessions != null) {
            return new ArrayList<>(userSessions.keySet());
        }
        return Collections.emptyList();
    }
}

