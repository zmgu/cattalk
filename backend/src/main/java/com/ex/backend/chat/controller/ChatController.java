package com.ex.backend.chat.controller;

import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String roomId) {
        List<ChatMessage> messages = chatService.getChatMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage) {

        chatService.saveMessage(chatMessage);
        String destination = "/stomp/sub/chat/" + chatMessage.getRoomId();
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

}
