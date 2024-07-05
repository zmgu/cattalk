package com.ex.backend.chat.service;

import com.ex.backend.chat.entity.ChatMessage;
import com.ex.backend.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final Logger logger = Logger.getLogger(ChatService.class.getName());

    public void saveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySendTimeAsc(roomId);
    }

}
