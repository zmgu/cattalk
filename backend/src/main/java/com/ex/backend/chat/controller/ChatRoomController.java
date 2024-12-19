package com.ex.backend.chat.controller;

import com.ex.backend.chat.dto.ChatRoomListDto;
import com.ex.backend.chat.dto.CreateGroupChatRoomDto;
import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<String> createChatRoom(@RequestBody CreateChatRoomDto createChatRoomDto) {

        String createdRoom = chatRoomService.createChatRoom(createChatRoomDto);
        return ResponseEntity.ok(createdRoom);
    }

    @PostMapping("/group")
    public ResponseEntity<String> createGroupChatRoom(@RequestParam String chatRoomName, @RequestParam List<Long> userIds) {

        String createdRoom = chatRoomService.createGroupChatRoom(chatRoomName, userIds);
        return ResponseEntity.ok(createdRoom);
    }

    @GetMapping
    public List<ChatRoomListDto> findChatRoomList(@RequestParam Long userId) {

        return chatRoomService.findChatRoomList(userId);
    }

    @GetMapping("/find")
    public ResponseEntity<String> findChatRoom(@RequestParam Long myUserId, @RequestParam Long friendUserId) {

        String roomId = chatRoomService.findChatRoom(myUserId, friendUserId);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/{roomId}/name")
    public ResponseEntity<String> findChatRoomName(@PathVariable String roomId, @RequestParam Long userId) {
        String chatRoomName = chatRoomService.findChatRoomName(roomId, userId);

        return ResponseEntity.ok(chatRoomName);
    }

    // RDB 에서 가져오는 최근 메시지 읽은 시간 리스트
    @GetMapping("/{roomId}/times")
    public Map<Long, Date> getLastReadTimes(@RequestParam Long userId, @PathVariable String roomId) {
        return chatRoomService.getLastReadTimes(userId, roomId);
    }
}
