package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatRoomName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomNameRepository extends JpaRepository<ChatRoomName, String> {

    List<ChatRoomName> findByUserId(Long userId);

}
