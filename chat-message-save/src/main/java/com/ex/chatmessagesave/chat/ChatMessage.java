package com.ex.chatmessagesave.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomId;
    private Long senderUserId;
    private String senderNickname;
    private String content;
    private Date sendTime;
    private String type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}

