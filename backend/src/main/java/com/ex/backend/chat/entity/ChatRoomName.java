package com.ex.backend.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String roomId;
    private Long userId;
    private String roomName;

}