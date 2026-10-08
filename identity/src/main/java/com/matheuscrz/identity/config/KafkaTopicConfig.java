package com.matheuscrz.identity.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic userEventsTopic() {
        return TopicBuilder.name("identity.user-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic userEventsDeadLetterTopic() {
        return TopicBuilder.name("identity.user-events.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}