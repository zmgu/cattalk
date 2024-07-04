package com.ex.backend.websocket;

import com.ex.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatService chatService;
    private final Logger logger = Logger.getLogger(WebSocketEventListener.class.getName());

    /*
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.info("handleWebSocketConnectListener 동작중");
    }
    */

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        logger.info("webSocketDisconnectListener 동작중");
    }
}
