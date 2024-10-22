package com.ex.backend.kafka;

import com.ex.backend.chat.entity.ChatMessage;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessage> chatKafkaListenerContainerFactory() {
        return getContainerFactory("chat", ChatMessage.class);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> getContainerFactory(String groupId, Class<T> classType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(getKafkaConsumerFactory(groupId, classType));
        return factory;
    }

    private <T> DefaultKafkaConsumerFactory<String, T> getKafkaConsumerFactory(String groupId,Class<T> classType) {
        JsonDeserializer<T> deserializer = setDeserializer(classType);
        return new DefaultKafkaConsumerFactory<>(setConfig(groupId, deserializer), new StringDeserializer(), deserializer);
    }

    private <T> ImmutableMap<String, Object> setConfig(String groupId, JsonDeserializer<T> deserializer) {
        ImmutableMap<String, Object> config = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                .put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"1")
                .build();
        return config;
    }

    private <T> JsonDeserializer<T> setDeserializer(Class<T> classType) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(classType, false);
        return deserializer;
    }
}
