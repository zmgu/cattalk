package com.ex.backend.chat.service;

import com.ex.backend.chat.domain.ChatMessage;
import com.ex.backend.chat.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageMapper chatMessageMapper;
    private final Logger logger = Logger.getLogger(ChatService.class.getName());

    public void saveMessage(ChatMessage chatMessage) {
        chatMessageMapper.insertChatMessage(chatMessage);
    }

    public List<ChatMessage> getChatMessagesByRoomId(String roomId) {
        return chatMessageMapper.getChatMessagesByRoomId(roomId);
    }

}
