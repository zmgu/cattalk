package com.ex.backend.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaAdmin kafkaAdmin;

    private NewTopic createTopic(String roomId) {

        return TopicBuilder.name(roomId)
                .partitions(10)
                .replicas(1)
                .build();
    }

    @PostConstruct
    public void init() {
        kafkaAdmin.createOrModifyTopics(createTopic("chat"));
    }
}
