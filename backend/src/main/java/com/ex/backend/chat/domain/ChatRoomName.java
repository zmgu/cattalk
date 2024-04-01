package com.ex.backend.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomName {

    private final String roomId;
    private final Long userId;
    private String roomName;

}