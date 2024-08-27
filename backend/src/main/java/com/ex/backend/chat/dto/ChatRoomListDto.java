package com.ex.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomListDto {

    private String roomId;
    private String roomName;
    private String content;
    private Date sendTime;
    
}
