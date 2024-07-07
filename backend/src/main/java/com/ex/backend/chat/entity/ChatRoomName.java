package com.ex.backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_room_name")
public class ChatRoomName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomNameId;
    private String roomId;
    private Long userId;
    private String roomName;

}