package com.ex.backend.kafka;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class KafkaUtil {

    public String groupIdKey(String roomId) {
        return roomId + " : " + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}
