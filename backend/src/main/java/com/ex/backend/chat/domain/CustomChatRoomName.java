package com.ex.backend.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomChatRoomName {

    private final String userId;
    private final String roomId;
    private String customRoomName;

}
