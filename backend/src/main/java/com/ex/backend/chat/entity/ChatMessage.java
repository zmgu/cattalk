package com.ex.backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "sender_user_id")
    private Long senderUserId;

    @Column(name = "sender_nickname")
    private String senderNickname;

    @Column(name = "content")
    private String content;

    @Column(name = "send_time")
    private Date sendTime;

    @Column(name = "type")
    private String type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
