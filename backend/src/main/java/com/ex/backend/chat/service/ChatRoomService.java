package com.ex.backend.chat.service;

import com.ex.backend.chat.domain.ChatRoom;
import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.mapper.ChatRoomMapper;
import com.ex.backend.chat.mapper.ChatRoomNameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Logger logger = Logger.getLogger(ChatRoomService.class.getName());
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomNameMapper chatRoomNameMapper;

    public String createRoom(Long myUserId,Long friendUserId, String friendNickname) {

        String roomId = UUID.randomUUID().toString();

        try {
            chatRoomMapper.createChatRoom(roomId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "createChatRoom 쿼리 에러", e);
        }

        ChatRoomName chatRoomName = ChatRoomName.builder()
                .roomId(roomId)
                .userId(myUserId)
                .roomName(friendNickname)
                .build();

        try {
            chatRoomNameMapper.createChatRoomName(chatRoomName);
        } catch (Exception e) {
            logger.severe("createChatRoomName 쿼리 에러 : " + e);
        }

        try {
            chatRoomMapper.insertChatRoomParticipant(roomId, myUserId);
            chatRoomMapper.insertChatRoomParticipant(roomId, friendUserId);
        } catch (Exception e) {
            logger.severe("insertChatRoomParticipant 쿼리 에러 : " + e);
        }

        return roomId;
    }

}
