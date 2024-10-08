package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ChatRoomListMessageResponseDto {

    private String roomId;
    private String content;
    private Date sendTime;
}
