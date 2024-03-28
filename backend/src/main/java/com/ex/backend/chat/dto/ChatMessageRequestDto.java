package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ChatMessageRequestDto {

    private String roomId;
    private String content;
}
