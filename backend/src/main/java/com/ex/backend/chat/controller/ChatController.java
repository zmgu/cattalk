package com.ex.backend.chat.controller;

import com.ex.backend.chat.domain.ChatRoom;
import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final Logger logger = Logger.getLogger(ChatController.class.getName());
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<String> createRoom(@RequestBody CreateChatRoomDto request) {
        try {
            String createdRoom = chatRoomService.createRoom(request.getMyUserId(), request.getFriendUserId(), request.getFriendNickname());

            return ResponseEntity.ok(createdRoom);

        } catch (Exception e) {
            logger.severe("채팅방 생성 에러 : " + e);
            return ResponseEntity.badRequest().build();
        }
    }
}