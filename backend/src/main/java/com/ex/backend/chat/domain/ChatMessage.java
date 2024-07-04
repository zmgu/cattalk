package com.ex.backend.chat.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long id;
    private MessageType type;
    private Long senderUserId;
    private String senderNickname;
    private String content;
    private String roomId;
    private Date sendTime;
    private String readBy;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
