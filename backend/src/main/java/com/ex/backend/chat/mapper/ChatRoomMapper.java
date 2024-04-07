package com.ex.backend.chat.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRoomMapper {

    void createChatRoom(String roomId) throws Exception;

    void insertChatRoomParticipant(String roomId, Long userId) throws Exception;

    String findChatRoom(Long myUserId, Long friendUserId) throws Exception;
}
