package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatRoomName;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatRoomNameMapper {

    void createChatRoomName(ChatRoomName chatRoomName) throws Exception;

    String findChatRoomName(@Param("roomId") String roomId, @Param("userId") Long userId) throws Exception;

}
