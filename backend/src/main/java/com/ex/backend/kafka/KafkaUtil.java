package com.ex.backend.kafka;

import com.ex.backend.chat.entity.ChatMessage;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class KafkaUtil {

    public String groupIdKey(ChatMessage chatMessage) {
        return chatMessage.getRoomId() + " : " + new SimpleDateFormat("yyyyMMdd").format(chatMessage.getSendTime());
    }
}
