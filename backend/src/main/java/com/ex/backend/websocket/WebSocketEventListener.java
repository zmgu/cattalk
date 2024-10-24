package com.ex.backend.websocket;

import com.ex.backend.chat.service.BroadcastService;
import com.ex.backend.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final JwtProvider jwtProvider;
    private final BroadcastService broadcast;

    /**
     *  웹소켓 연결 후 같은 채팅방 웹소켓 연결된 유저에게 내 정보 브로드캐스트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        List<String> roomIdHeader = headerAccessor.getNativeHeader("RoomId");

        if (roomIdHeader != null && !roomIdHeader.isEmpty()) {
            List<String> accessToken = headerAccessor.getNativeHeader("Authorization");
            Long userId = jwtProvider.getUserId(accessToken.get(0));
            String roomId = roomIdHeader.get(0);
            broadcast.broadcastLastReadTime(roomId, userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    }
}