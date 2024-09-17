package com.ex.backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_room_participant")
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomParticipantId;
    private String roomId;
    private Long userId;
    private String roomName;
    private Date lastMessageReadAt;
    private Date JoinedAt;

}