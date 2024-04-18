package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatRoomName;
import com.ex.backend.chat.dto.FindChatRoomDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatRoomMapper {

    void createChatRoom(String roomId) throws Exception;

    void insertChatRoomParticipant(String roomId, Long userId) throws Exception;

    String findChatRoom(Long myUserId, Long friendUserId) throws Exception;

    List<ChatRoomName> findChatRoomList(ChatRoomName chatRoomName) throws Exception;
}
