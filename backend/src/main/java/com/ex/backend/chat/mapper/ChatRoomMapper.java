package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatRoom;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRoomMapper {

    void createChatRoom(ChatRoom chatRoom) throws Exception;

}
