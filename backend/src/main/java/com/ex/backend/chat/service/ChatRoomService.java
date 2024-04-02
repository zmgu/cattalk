package com.ex.backend.chat.service;

import com.ex.backend.chat.domain.ChatRoom;
import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.mapper.ChatRoomMapper;
import com.ex.backend.chat.mapper.ChatRoomNameMapper;
import com.ex.backend.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Logger logger = Logger.getLogger(ChatRoomService.class.getName());
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomNameMapper chatRoomNameMapper;

    public ChatRoom createRoom(Long userId,String partnerName) {

        String roomId = UUID.randomUUID().toString();

        ChatRoom room = ChatRoom.builder()
                .roomId(roomId)
                .createdDate(new Date(System.currentTimeMillis()))
                .build();

        try {
            chatRoomMapper.createChatRoom(room);
        } catch (Exception e) {
            logger.severe("createChatRoom 생성 에러 : " + e);
        }

        ChatRoomName chatRoomName = ChatRoomName.builder()
                .roomId(roomId)
                .userId(userId)
                .roomName(partnerName)
                .build();

        try {
            chatRoomNameMapper.createChatRoomName(chatRoomName);
        } catch (Exception e) {
            logger.severe("createChatRoomName 생성 에러 : " + e);
        }

        return room;
    }

}
