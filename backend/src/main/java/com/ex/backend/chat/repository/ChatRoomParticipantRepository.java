package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatRoomParticipant;
import com.ex.backend.chat.entity.ChatRoomParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, ChatRoomParticipantId> {

    @Query("SELECT crp.roomId FROM ChatRoomParticipant crp " +
            "JOIN ChatRoomParticipant crp2 ON crp.roomId = crp2.roomId " +
            "WHERE crp.userId = :myUserId AND crp2.userId = :friendUserId")
    String findChatRoomIdByUserIds(@Param("myUserId") Long myUserId, @Param("friendUserId") Long friendUserId);
}
