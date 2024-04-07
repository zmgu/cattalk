package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindChatRoomDto {

    private Long myUserId;
    private Long friendUserId;

}
