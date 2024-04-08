package com.ex.backend.chat.service;

import com.ex.backend.chat.domain.ChatRoom;
import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.dto.FindChatRoomDto;
import com.ex.backend.chat.mapper.ChatRoomMapper;
import com.ex.backend.chat.mapper.ChatRoomNameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Logger logger = Logger.getLogger(ChatRoomService.class.getName());
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomNameMapper chatRoomNameMapper;

    public String createChatRoom(CreateChatRoomDto createChatRoomDto) {

        String roomId = UUID.randomUUID().toString();

        try {
            chatRoomMapper.createChatRoom(roomId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "createChatRoom 쿼리 에러", e);
        }

        ChatRoomName myChatRoomName = ChatRoomName.builder()
                .roomId(roomId)
                .userId(createChatRoomDto.getMyUserId())
                .roomName(createChatRoomDto.getFriendNickname())
                .build();

        ChatRoomName friendChatRoomName = ChatRoomName.builder()
                .roomId(roomId)
                .userId(createChatRoomDto.getFriendUserId())
                .roomName(createChatRoomDto.getMyNickname())
                .build();

        try {
            chatRoomNameMapper.createChatRoomName(myChatRoomName);
            chatRoomNameMapper.createChatRoomName(friendChatRoomName);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "createChatRoomName 쿼리 에러", e);
        }

        try {
            chatRoomMapper.insertChatRoomParticipant(roomId, createChatRoomDto.getMyUserId());
            chatRoomMapper.insertChatRoomParticipant(roomId, createChatRoomDto.getFriendUserId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "insertChatRoomParticipant 쿼리 에러", e);
        }

        return roomId;
    }

    public String findChatRoom(FindChatRoomDto findChatRoomDto) {

        String roomId = null;

        try {
            roomId = chatRoomMapper.findChatRoom(findChatRoomDto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "findChatRoom 쿼리 에러", e);
        }

        return roomId;
    }
}
