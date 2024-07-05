package com.ex.backend.chat.repository;

import com.ex.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    boolean existsById(Long id);
    List<ChatMessage> findByRoomIdOrderBySendTimeAsc(String roomId);
}
