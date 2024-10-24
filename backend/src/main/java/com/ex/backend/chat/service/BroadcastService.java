package com.ex.backend.chat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     *  채팅방 접속 시, 채팅방 웹소켓에 연결되어 있는 유저에게 내 lastReadTime 보내기 위한 메서드
     *  SimpMessagingTemplate 의 순환 참조 방지를 위해 분리됨
     */
    public void broadcastLastReadTime(String roomId, Long userId) {
        Map<Long, Date> lastReadTimes = new HashMap<>();
        lastReadTimes.put(userId, new Date());

        messagingTemplate.convertAndSend("/stomp/sub/chat/" + roomId + "/lastReadTime", lastReadTimes);

    }
}
