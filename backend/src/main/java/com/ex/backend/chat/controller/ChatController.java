package com.ex.backend.chat.controller;

import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

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
}