package com.ex.backend.websocket;

import com.ex.backend.chat.service.ChatRoomService;
import com.ex.backend.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ChatRoomService chatRoomService;
    private final JwtProvider jwtProvider;
    private final Logger logger = Logger.getLogger(WebSocketChannelInterceptor.class.getName());


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();


        switch (accessor.getCommand()) {
            case CONNECT -> {
                String roomId = accessor.getFirstNativeHeader("roomId");
                Long userId = jwtProvider.getUserId(accessor.getFirstNativeHeader("Authorization"));

                webSocketSessionManager.addUserSession(roomId, userId, sessionId);
                logger.info("채팅방 : " + roomId + ", 접속 유저 목록 : " + webSocketSessionManager.getRoomAllUserByRoomId(roomId));
            }
            case SUBSCRIBE -> {
            }
            case DISCONNECT -> {
                /*
                 *  채팅방을 나가면서 웹소켓 세션 삭제, 내 Redis 정보 업데이트, RDB 읽은 시간 업데이트
                 *  만약 내 세션이 채팅방의 마지막 세션이었다면 채팅방 정보 전체를 RDB 읽은 시간 업데이트 후 Redis 삭제
                 * */
                String roomId = webSocketSessionManager.getRoomIdBySessionId(sessionId);
                Long userId = webSocketSessionManager.getUserIdBySessionId(sessionId);

                if (roomId != null) {
                    chatRoomService.updateLastReadTimeRedis(userId, roomId, new Date());
                    webSocketSessionManager.removeUserSession(sessionId);

                    logger.info("채팅방 : " + roomId + ", 접속 유저 목록 : " + webSocketSessionManager.getRoomAllUserByRoomId(roomId));

                    if (webSocketSessionManager.getRoomAllUserByRoomId(roomId).isEmpty()) {
                        logger.info("채팅방 웹소켓 연결된 유저가 없음 -> 채팅방 redis 전체 삭제");
                        chatRoomService.deleteChatRoomRedis(roomId);
                    }
                }
            }
        }

        return message;
    }
}

