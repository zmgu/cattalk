package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatRoomName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomNameRepository extends JpaRepository<ChatRoomName, String> {
    @Query("SELECT crn.roomName FROM ChatRoomName crn WHERE crn.roomId = :roomId AND crn.userId = :userId")
    String findRoomNameByRoomIdAndUserId(@Param("roomId") String roomId, @Param("userId") Long userId);

    List<ChatRoomName> findByUserId(Long userId);
}
