package com.ex.backend.chat.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ChatMessage {

    private final MessageType type;
    private final Long senderUserId;
    private final String content;
    private final String roomId;
    private final Date sendTime;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}