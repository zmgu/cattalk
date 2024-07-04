package com.ex.backend.chat.mapper;

import com.ex.backend.chat.domain.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Select("SELECT COUNT(*) > 0 " +
            "FROM chat_message " +
            "WHERE id = #{id}")
    boolean existsById(Long id);

    @Insert("INSERT INTO chat_message(" +
            "  room_id" +
            ", sender_user_id" +
            ", sender_nickname" +
            ", content, send_time" +
            ", read_by" +
            ", type)" +

            " VALUES(" +

            "  #{roomId}" +
            ", #{senderUserId}" +
            ", #{senderNickname}" +
            ", #{content}, #{sendTime}" +
            ", #{readBy}" +
            ", #{type})")
    void insertChatMessage(ChatMessage chatMessage);

    @Select("SELECT * FROM chat_message " +
            "WHERE room_id = #{roomId} " +
            "ORDER BY send_time ASC")
    List<ChatMessage> getChatMessagesByRoomId(String roomId);
}
