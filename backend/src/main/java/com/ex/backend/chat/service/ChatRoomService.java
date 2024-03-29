package com.ex.backend.chat.service;

import com.ex.backend.chat.domain.ChatRoom;
import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatMapper chatMapper;

    public ChatRoom createRoom(Long userId,String partnerName) throws Exception {

        String roomId = UUID.randomUUID().toString();

        ChatRoom room = ChatRoom.builder()
                .roomId(roomId)
                .createdDate(new Date(System.currentTimeMillis()))
                .build();

        chatMapper.createRoom(room);

        ChatRoomName chatRoomName = ChatRoomName.builder()
                .roomId(roomId)
                .userId(userId)
                .roomName(partnerName)
                .build();

        chatMapper.createRoomName(chatRoomName);

        return room;
    }

}
