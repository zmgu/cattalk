package com.ex.backend.chat.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ChatMessage {

    private String messageId;
    private String userName;
    private String content;
    private String roomId;
    private Date sendTime;

}