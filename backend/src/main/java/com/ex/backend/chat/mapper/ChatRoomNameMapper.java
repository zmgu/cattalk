package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatRoomName;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRoomNameMapper {

    void createChatRoomName(ChatRoomName chatRoomName) throws Exception;

}
