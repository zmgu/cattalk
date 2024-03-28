package com.ex.backend.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ChatRoom {

    private final String roomId;
    private final Date createdDate;
    private Date lastMessageTime;

}
