package com.ex.backend.websocket;

import com.ex.backend.chat.service.ChatRoomService;
import com.ex.backend.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final ChatRoomService chatRoomService;
    private final JwtProvider jwtProvider;
    private final ChatRoomWebSocketSessionManager chatRoomSessionManager;
    private final ChatRoomListWebSocketSessionManager chatRoomListSessionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        switch (accessor.getCommand()) {
            case CONNECT -> {
                Long userId = jwtProvider.getUserId(accessor.getFirstNativeHeader("Authorization"));
                String type = accessor.getFirstNativeHeader("Type");

                log.info("type : {} ", type);

                switch (type) {
                    case "chatRoom" -> {
                        String roomId = accessor.getFirstNativeHeader("RoomId");
                        chatRoomSessionManager.addUserSession(roomId, userId, sessionId);
                        log.info("채팅방 : {}, 접속 유저 목록 : {}", roomId, chatRoomSessionManager.getRoomAllUserByRoomId(roomId));
                        log.info("채팅방 : {} 을 들어가지 않고 채팅방 목록에 접속한 유저 목록 : {}", roomId, chatRoomListSessionManager.getRoomAllUserByRoomId(roomId));
                    }

                    case "chatRoomList" -> {
                        String roomIds = accessor.getFirstNativeHeader("RoomIds");
                        String[] roomIdList = roomIds.split(",");

                        for (String roomId : roomIdList) {
                            chatRoomListSessionManager.addUserSession(roomId, userId, sessionId);
                            log.info("채팅방 : {} 을 들어가지 않고 채팅방 목록에 접속한 유저 목록 : {}", roomId, chatRoomListSessionManager.getRoomAllUserByRoomId(roomId));
                        }
                    }
                }
            }

            case SUBSCRIBE -> {

            }

            case DISCONNECT -> {

                if (chatRoomListSessionManager.sessionExists(sessionId)) {
                    chatRoomListSessionManager.removeUserSession(sessionId);
                    log.info("채팅방 목록 접속 해제 체크 : {}", chatRoomListSessionManager.getRoomIdsBySessionId(sessionId));

                } else {
                    /**
                     *  채팅방을 나가면서 웹소켓 세션 삭제, 내 Redis 정보 업데이트, RDB 읽은 시간 업데이트
                     *  만약 내 세션이 채팅방의 마지막 세션이었다면 채팅방 정보 전체를 RDB 읽은 시간 업데이트 후 Redis 삭제
                     */
                    String roomId = chatRoomSessionManager.getRoomIdBySessionId(sessionId);
                    Long userId = chatRoomSessionManager.getUserIdBySessionId(sessionId);

                    if (roomId != null) {
                        chatRoomService.updateLastReadTime(userId, roomId, new Date());
                        chatRoomSessionManager.removeUserSession(sessionId);

                        log.info("채팅방 : {}, 접속 유저 목록 : {}", roomId, chatRoomSessionManager.getRoomAllUserByRoomId(roomId));

                        if (chatRoomSessionManager.getRoomAllUserByRoomId(roomId).isEmpty()) {
                            log.info("채팅방 웹소켓 연결된 유저가 없음 -> 채팅방 redis 전체 삭제");
                            chatRoomService.deleteChatRoomRedis(roomId);
                        }
                    }
                }
            }
        }

        return message;
    }
}
