package com.ex.backend.chat.service;

import com.ex.backend.chat.dto.ChatRoomListDto;
import com.ex.backend.chat.dto.CreateChatRoomDto;
import com.ex.backend.chat.entity.ChatRoom;
import com.ex.backend.chat.entity.ChatRoomParticipant;
import com.ex.backend.chat.repository.ChatRoomParticipantRepository;
import com.ex.backend.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatRedis chatRedis;

    public String createChatRoom(CreateChatRoomDto createChatRoomDto) {
        String roomId = UUID.randomUUID().toString();

        Date now = new Date();

        try {
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomId(roomId)
                    .createDate(now)
                    .build();

            chatRoomRepository.save(chatRoom);
        } catch (Exception e) {
            log.error("createChatRoom 쿼리 에러 {}", e.getMessage());
        }

        ChatRoomParticipant chatRoomParticipant = ChatRoomParticipant.builder()
                .roomId(roomId)
                .userId(createChatRoomDto.getMyUserId())
                .roomName(createChatRoomDto.getFriendNickname())
                .lastMessageReadAt(now)
                .JoinedAt(now)
                .build();

        ChatRoomParticipant friendChatRoomParticipant = ChatRoomParticipant.builder()
                .roomId(roomId)
                .userId(createChatRoomDto.getFriendUserId())
                .roomName(createChatRoomDto.getMyNickname())
                .lastMessageReadAt(now)
                .JoinedAt(now)
                .build();

        try {
            chatRoomParticipantRepository.save(chatRoomParticipant);
            chatRoomParticipantRepository.save(friendChatRoomParticipant);
        } catch (Exception e) {
            log.error("createChatRoomParticipant 쿼리 에러 {}", e.getMessage());
        }

        return roomId;
    }

    public String findChatRoom(Long myUserId, Long friendUserId) {
        String roomId = null;
        try {
            roomId = chatRoomRepository.findChatRoomIdByUserIds(myUserId, friendUserId);
        } catch (Exception e) {
            log.error("findChatRoom 쿼리 에러 {}", e.getMessage());
        }
        return roomId;
    }

    public List<ChatRoomListDto> findChatRoomList(Long userId) {
        List<ChatRoomListDto> chatRoomList = null;
        try {
            chatRoomList = chatRoomRepository.findChatRoomListByUserId(userId);
        } catch (Exception e) {
            log.error("findChatRoomList 쿼리 에러 {}", e.getMessage());
        }
        return chatRoomList;
    }

    public String findChatRoomName(String roomId, Long userId) {
        String chatRoomName = null;
        try {
            chatRoomName = chatRoomRepository.findRoomNameByRoomIdAndUserId(roomId, userId);
        } catch (Exception e) {
            log.error("findChatRoomName 쿼리 에러 {}", e.getMessage());
        }
        return chatRoomName;
    }

    public Map<Long, Date> getLastReadTimes(Long userId, String roomId) {

        Map<Long, Date> lastReadTimes = new HashMap<>();

        try {
            String exist = chatRedis.getUserChatInfo(userId, roomId, "lastMessageReadAt");
            log.info("exist: {}", exist);

            // 레디스에 데이터가 있을 경우 본인 데이터만 갱신 후 종료
            if (exist != null) {
                Date currentTime = new Date();

                updateLastReadTimeRedis(userId, roomId, currentTime);

                lastReadTimes.put(userId, currentTime);

                return lastReadTimes;
            }

            // 레디스에 데이터가 없을 경우 진행
            List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findChatRoomParticipantByRoomId(roomId);

            for (ChatRoomParticipant participant : participants) {
                Long participantId = participant.getUserId();

                Date lastMessageReadAt =
                        participantId.equals(userId) ? new Date() : participant.getLastMessageReadAt();

                // 레디스에 최근 읽은 시간 저장
                updateLastReadTimeRedis(participantId, roomId, lastMessageReadAt);

                lastReadTimes.put(participantId, lastMessageReadAt);
            }
        } catch (Exception e) {
            log.error("findChatRoomParticipantByRoomId 쿼리 에러 {}", e.getMessage());
        }

        return lastReadTimes;
    }

    // 레디스에서 가져온 읽은 시간으로 RDB 저장 후 삭제
    public void deleteChatRoomRedis(String roomId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findChatRoomParticipantByRoomId(roomId);

        for (ChatRoomParticipant participant : participants) {
            Long userId = participant.getUserId();

                String lastMessageReadAtString = chatRedis.getUserChatInfo(userId, roomId, "lastMessageReadAt");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date lastMessageReadAt = null;
                try {
                    lastMessageReadAt = dateFormat.parse(lastMessageReadAtString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            chatRoomParticipantRepository.updateLastMessageReadAt(userId, roomId, lastMessageReadAt);
            chatRedis.deleteUserChatInfo(userId, roomId);
        }
    }

    public void updateLastReadTimeRedis(Long userId, String roomId, Date lastMessageReadAt) {
        chatRedis.deleteUserChatInfo(userId, roomId);
        chatRedis.saveUserChatInfo(userId, roomId, lastMessageReadAt);
    }

    public void updateLastReadTime(Long userId, String roomId, Date lastMessageReadAt) {
        chatRedis.deleteUserChatInfo(userId, roomId);
        chatRedis.saveUserChatInfo(userId, roomId, lastMessageReadAt);
        chatRoomParticipantRepository.updateLastMessageReadAt(userId, roomId, lastMessageReadAt);

    }
}
