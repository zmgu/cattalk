package com.ex.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private String id; // 채팅방의 고유 식별자
    private String name; // 채팅방 이름
    private ChatType type; // 채팅방 타입 (예: PRIVATE, GROUP)

    public enum ChatType {
        PRIVATE, GROUP
    }
}
