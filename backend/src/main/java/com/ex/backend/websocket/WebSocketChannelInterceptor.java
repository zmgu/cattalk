package com.ex.backend.websocket;

import com.ex.backend.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final WebSocketSessionManager webSocketSessionManager;

    private final Logger logger = Logger.getLogger(WebSocketChannelInterceptor.class.getName());

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();


        switch (accessor.getCommand()) {
            case CONNECT: {
                String roomId = accessor.getFirstNativeHeader("roomId");
                Long userId = jwtProvider.getUserId(accessor.getFirstNativeHeader("Authorization"));

                // 웹소켓 연결 중인 채팅방에 유저 추가 및 읽은 시간 레디스 갱신
                webSocketSessionManager.addUserSession(roomId, userId, sessionId);

                logger.info("채팅방 " + roomId + " 접속 유저 목록 : " + webSocketSessionManager.getRoomAllUserByRoomId(roomId));
                break;
            }
            case SUBSCRIBE: {
            }
            case DISCONNECT: {
                break;
            }
        }
        return message;
    }
}

