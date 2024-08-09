package com.ex.backend.chat.service;

import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.entity.ChatRoom;
import com.ex.backend.chat.entity.ChatRoomName;
import com.ex.backend.chat.repository.ChatRoomNameRepository;
import com.ex.backend.chat.repository.ChatRoomRepository;
import com.ex.backend.kafka.service.KafkaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Logger logger = Logger.getLogger(ChatRoomService.class.getName());
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomNameRepository chatRoomNameRepository;
    private final KafkaAdminService kafkaAdminService;

    public String createChatRoom(CreateChatRoomDto createChatRoomDto) {
        String roomId = UUID.randomUUID().toString();
        kafkaAdminService.createChatRoomTopic(roomId);
        
        try {
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomId(roomId)
                    .build();

            chatRoomRepository.save(chatRoom);
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
            chatRoomNameRepository.save(myChatRoomName);

            logger.info("================= 처음 save 완료 ==============");
            chatRoomNameRepository.save(friendChatRoomName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "createChatRoomName 쿼리 에러", e);
        }

        kafkaAdminService.createChatRoomTopic(roomId);
        return roomId;
    }

    public String findChatRoom(Long myUserId, Long friendUserId) {
        String roomId = null;
        try {
            roomId = chatRoomRepository.findChatRoomIdByUserIds(myUserId, friendUserId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "findChatRoom 쿼리 에러", e);
        }
        return roomId;
    }

    public List<ChatRoomName> findChatRoomList(Long userId) {
        List<ChatRoomName> chatRoomList = null;
        try {
            chatRoomList = chatRoomNameRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "findChatRoomList 쿼리 에러", e);
        }
        return chatRoomList;
    }

    public String findChatRoomName(String roomId, Long userId) {
        String chatRoomName = null;
        try {
            chatRoomName = chatRoomRepository.findRoomNameByRoomIdAndUserId(roomId, userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "findChatRoomName 쿼리 에러", e);
        }
        return chatRoomName;
    }
}
