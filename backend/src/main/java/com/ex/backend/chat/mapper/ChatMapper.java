package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatRoom;
import com.ex.backend.chat.domain.ChatRoomName;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMapper {

    void createRoom(ChatRoom chatRoom) throws Exception;

    void createRoomName(ChatRoomName chatRoomName) throws Exception;
}
