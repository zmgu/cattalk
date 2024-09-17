package com.ex.backend.chat.dto;

import com.ex.backend.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {

    private ChatMessage chatMessage;
    private Map<Long, Date> lastReadTimes;
}
