package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatRoomParticipant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    @Query("SELECT c FROM ChatRoomParticipant c WHERE c.roomId = :roomId")
    List<ChatRoomParticipant> findChatRoomParticipantByRoomId(@Param("roomId") String roomId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatRoomParticipant c SET c.lastMessageReadAt = :lastMessageReadAt WHERE c.userId = :userId AND c.roomId = :roomId")
    int updateLastMessageReadAt(@Param("userId") Long userId,
                                @Param("roomId") String roomId,
                                @Param("lastMessageReadAt") Date lastMessageReadAt);
}
