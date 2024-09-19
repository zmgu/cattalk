package com.ex.backend.websocket;

import com.ex.backend.chat.service.ChatRoomService;
import com.ex.backend.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final JwtProvider jwtProvider;
    private final ChatRoomService chatRoomService;
    private final WebSocketSessionManager webSocketSessionManager;
    private final Logger logger = Logger.getLogger(WebSocketEventListener.class.getName());

    /*
    *  웹소켓 연결된 후 같은 웹소켓 연결된 유저에게 내 정보 브로드캐스트
    * */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        List<String> accessToken = headerAccessor.getNativeHeader("Authorization");
        List<String> roomIdHeader = headerAccessor.getNativeHeader("roomId");

        Long userId = jwtProvider.getUserId(accessToken.get(0));
        String roomId = roomIdHeader.get(0);
        String sessionId = headerAccessor.getSessionId();

        webSocketSessionManager.addUserSession(roomId, userId, sessionId);
        logger.info("채팅방 : " + roomId + ", 접속 유저 목록 : " + webSocketSessionManager.getRoomAllUserByRoomId(roomId));

        chatRoomService.broadcastLastReadTime(roomId, userId);
    }

    /*
     *  채팅방을 나가면서 웹소켓 세션 삭제, 내 Redis 정보 업데이트, RDB 읽은 시간 업데이트
     *  만약 내 세션이 채팅방의 마지막 세션이었다면 채팅방 정보 전체를 RDB 읽은 시간 업데이트 후 Redis 삭제
     * */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String roomId = webSocketSessionManager.getRoomIdBySessionId(sessionId);
        Long userId = webSocketSessionManager.getUserIdBySessionId(sessionId);

        chatRoomService.updateLastReadTimeRedis(userId, roomId, new Date());
        webSocketSessionManager.removeUserSession(sessionId);

        logger.info("채팅방 : " + roomId + ", 접속 유저 목록 : " + webSocketSessionManager.getRoomAllUserByRoomId(roomId));
        if(webSocketSessionManager.getRoomAllUserByRoomId(roomId).size() < 1) {
            logger.info("채팅방 웹소켓 연결된 유저가 없음 -> 채팅방 redis 전체 삭제");
            chatRoomService.deleteChatRoomRedis(roomId);
        }
    }
}