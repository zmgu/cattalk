package com.ex.backend.chat.dto;

import com.ex.backend.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {

    private ChatMessage chatMessage;
    private Map<Long, Date> lastReadTimes;
}
