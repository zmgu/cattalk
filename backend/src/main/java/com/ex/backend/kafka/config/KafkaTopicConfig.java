package com.ex.backend.kafka.config;

import com.ex.backend.kafka.constants.KafkaConstants;
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

    private NewTopic createTopic(String topic) {

        return TopicBuilder.name(topic)
                .partitions(KafkaConstants.CHAT_PARTITIONS)
                .replicas(1)
                .build();
    }

    @PostConstruct
    public void init() {
        kafkaAdmin.createOrModifyTopics(createTopic(KafkaConstants.CHAT_TOPIC));
    }
}
