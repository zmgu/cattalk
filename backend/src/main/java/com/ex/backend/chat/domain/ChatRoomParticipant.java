package com.ex.backend.chat.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomParticipant {

    private Long participantId;
    private String roomId;
    private Long userId;
    private Date joinedAt;
    private String status;
}
