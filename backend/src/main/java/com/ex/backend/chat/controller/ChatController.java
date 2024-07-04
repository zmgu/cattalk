package com.ex.backend.chat.controller;

import com.ex.backend.chat.domain.ChatMessage;
import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.service.ChatRoomService;
import com.ex.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Logger logger = Logger.getLogger(ChatController.class.getName());

    @PostMapping("/rooms")
    public ResponseEntity<String> createChatRoom(@RequestBody CreateChatRoomDto createChatRoomDto) {

        String createdRoom = chatRoomService.createChatRoom(createChatRoomDto);
        return ResponseEntity.ok(createdRoom);
    }

    @GetMapping("/rooms")
    public List<ChatRoomName> findChatRoomList(@RequestParam Long userId) {

        return chatRoomService.findChatRoomList(userId);
    }

    @GetMapping("/rooms/find")
    public ResponseEntity<String> findChatRoom(@RequestParam Long myUserId, @RequestParam Long friendUserId) {

        String roomId = chatRoomService.findChatRoom(myUserId, friendUserId);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/rooms/{roomId}/name")
    public ResponseEntity<String> findChatRoomName(@PathVariable String roomId, @RequestParam Long userId) {
        String chatRoomName = chatRoomService.findChatRoomName(roomId, userId);

        return ResponseEntity.ok(chatRoomName);
    }

    @GetMapping("/rooms/{roomId}/messages")
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
