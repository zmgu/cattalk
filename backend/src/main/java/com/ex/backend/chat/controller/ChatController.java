package com.ex.backend.chat.controller;

import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final Logger logger = Logger.getLogger(ChatController.class.getName());
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<String> createChatRoom(@RequestBody CreateChatRoomDto createChatRoomDto) {

        String createdRoom = chatRoomService.createChatRoom(createChatRoomDto);
        return ResponseEntity.ok(createdRoom);
    }

    @GetMapping("/room")
    public ResponseEntity<String> findChatRoom(@RequestParam Long myUserId, @RequestParam Long friendUserId) {

        String roomId = chatRoomService.findChatRoom(myUserId, friendUserId);
        return ResponseEntity.ok(roomId);
    }
}