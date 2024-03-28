package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomChatRoomNameDto {

    private final String roomId;
    private String roomName;

}
