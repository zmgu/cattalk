package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ChatMessageResponseDto {

    private String messageId;
    private String roomId;
    private String senderName;
    private String content;
    private Date sendTime;

}
