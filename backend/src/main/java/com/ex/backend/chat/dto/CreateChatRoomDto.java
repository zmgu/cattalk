package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRoomDto {

    private Long userId;
    private String partnerName;
}