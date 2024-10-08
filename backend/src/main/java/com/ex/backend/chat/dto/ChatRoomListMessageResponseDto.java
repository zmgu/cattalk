package com.ex.backend.chat.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public class ChatRoomListMessageResponseDto {

    private String roomId;
    private String content;
    private Date sendTime;
}
