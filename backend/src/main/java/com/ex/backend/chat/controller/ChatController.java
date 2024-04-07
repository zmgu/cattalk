package com.ex.backend.chat.controller;

import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.dto.FindChatRoomDto;
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
    public ResponseEntity<String> createChatRoom(@RequestBody CreateChatRoomDto request) {
        try {
            String createdRoom = chatRoomService
                    .createChatRoom(
                            request.getMyUserId()
                            ,request.getMyNickname()
                            ,request.getFriendUserId()
                            ,request.getFriendNickname()
                    );

            return ResponseEntity.ok(createdRoom);

        } catch (Exception e) {
            logger.severe("채팅방 생성 에러 : " + e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/room")
    public ResponseEntity<String> findChatRoom(@RequestBody FindChatRoomDto findChatRoomDto) {



        return null;
    }
}