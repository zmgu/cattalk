package com.ex.backend.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    private String roomId;
    private Date createdDate;
    private Date lastMessageTime;

}
