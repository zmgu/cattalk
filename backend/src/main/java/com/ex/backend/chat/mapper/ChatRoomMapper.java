package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatRoomName;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMapper {

    void createChatRoom(String roomId) throws Exception;

    void insertChatRoomParticipant(@Param("roomId") String roomId, @Param("userId") Long userId) throws Exception;

    String findChatRoom(@Param("myUserId") Long myUserId, @Param("friendUserId") Long friendUserId) throws Exception;

    List<ChatRoomName> findChatRoomList(Long myUserId) throws Exception;
}
