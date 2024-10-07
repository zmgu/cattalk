package com.ex.backend.chat.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListMessageResponseDto {

    private String roomId;
    private String content;
    private Date sendTime;
}
