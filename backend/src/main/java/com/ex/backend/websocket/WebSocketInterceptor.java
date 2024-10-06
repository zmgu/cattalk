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
public class WebSocketInterceptor implements ChannelInterceptor {

    private final ChatRoomWebSocketSessionManager chatRoomSessionManager;
    private final ChatRoomListWebSocketSessionManager chatRoomListSessionManager;
    private final ChatRoomService chatRoomService;
    private final JwtProvider jwtProvider;
    private final Logger logger = Logger.getLogger(WebSocketInterceptor.class.getName());


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        switch (accessor.getCommand()) {
            case CONNECT -> {
                Long userId = jwtProvider.getUserId(accessor.getFirstNativeHeader("Authorization"));
                String type = accessor.getFirstNativeHeader("Type");

                logger.info("type : " + type);

                if(type.equals("chatRoom")) {
                    String roomId = accessor.getFirstNativeHeader("RoomId");
                    chatRoomSessionManager.addUserSession(roomId, userId, sessionId);
                    logger.info("채팅방 : " + roomId + ", 접속 유저 목록 : " + chatRoomSessionManager.getRoomAllUserByRoomId(roomId));
                    logger.info("채팅방 목록 접속 유저 목록 : " + chatRoomListSessionManager.getRoomAllUserByRoomId(roomId));

                } else if(type.equals("chatRoomList")) {
                    String roomIds = accessor.getFirstNativeHeader("RoomIds");
                    String[] roomIdList = roomIds.split(",");

                    for (String roomId : roomIdList) {
                        chatRoomListSessionManager.addUserSession(roomId, userId, sessionId);
                        logger.info("채팅방 : " + roomId + ", 유저 ID : " + userId + " 세션 추가됨");
                        logger.info("채팅방목록 중:" + roomId + " 접속 유저 목록 : " + chatRoomListSessionManager.getRoomAllUserByRoomId(roomId));
                    }

                } else {

                }

            }

            case SUBSCRIBE -> {

            }

            case DISCONNECT -> {

                if (chatRoomListSessionManager.sessionExists(sessionId)) {
                    chatRoomListSessionManager.removeUserSession(sessionId);
                    logger.info("채팅방 목록 접속 해제 체크 : " + chatRoomListSessionManager.getRoomIdsBySessionId(sessionId));

                } else {
                    /*
                     *  채팅방을 나가면서 웹소켓 세션 삭제, 내 Redis 정보 업데이트, RDB 읽은 시간 업데이트
                     *  만약 내 세션이 채팅방의 마지막 세션이었다면 채팅방 정보 전체를 RDB 읽은 시간 업데이트 후 Redis 삭제
                     * */
                    String roomId = chatRoomSessionManager.getRoomIdBySessionId(sessionId);
                    Long userId = chatRoomSessionManager.getUserIdBySessionId(sessionId);

                    if (roomId != null) {
                        chatRoomService.updateLastReadTime(userId, roomId, new Date());
                        chatRoomSessionManager.removeUserSession(sessionId);

                        logger.info("채팅방 : " + roomId + ", 접속 유저 목록 : " + chatRoomSessionManager.getRoomAllUserByRoomId(roomId));

                        if (chatRoomSessionManager.getRoomAllUserByRoomId(roomId).isEmpty()) {
                            logger.info("채팅방 웹소켓 연결된 유저가 없음 -> 채팅방 redis 전체 삭제");
                            chatRoomService.deleteChatRoomRedis(roomId);
                        }
                    }
                }

            }
        }

        return message;
    }
}

