package com.ex.backend.kafka.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaAdminService {

    private final KafkaAdmin kafkaAdmin;

    public void createChatRoomTopic(String roomId) {
        NewTopic topic = TopicBuilder.name(roomId)
                .partitions(4)
                .replicas(1)
                .build();

        kafkaAdmin.createOrModifyTopics(topic);
    }
}
