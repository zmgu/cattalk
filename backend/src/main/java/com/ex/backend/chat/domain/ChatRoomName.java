package com.ex.backend.chat.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomName {

    private String roomId;
    private Long userId;
    private String roomName;

}