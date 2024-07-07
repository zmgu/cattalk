package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    boolean existsById(Long id);

    List<ChatMessage> findByRoomIdOrderBySendTimeAsc(String roomId);

}
