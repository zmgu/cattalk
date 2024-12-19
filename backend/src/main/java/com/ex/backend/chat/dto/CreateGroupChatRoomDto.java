package com.ex.backend.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateGroupChatRoomDto {

    private String chatRoomName;
    private List<Long> userIds;
}
