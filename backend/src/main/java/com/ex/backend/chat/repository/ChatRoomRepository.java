package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query( "SELECT crn.roomId " +
            "FROM ChatRoomName crn " +
            "JOIN ChatRoomName crn2 ON crn.roomId = crn2.roomId " +
            "WHERE crn.userId = :myUserId AND crn2.userId = :friendUserId" )
    String findChatRoomIdByUserIds(@Param("myUserId") Long myUserId, @Param("friendUserId") Long friendUserId);

    @Query( "SELECT crn.roomName " +
            "FROM ChatRoomName crn " +
            "WHERE crn.roomId = :roomId AND crn.userId = :userId" )
    String findRoomNameByRoomIdAndUserId(@Param("roomId") String roomId, @Param("userId") Long userId);

}
