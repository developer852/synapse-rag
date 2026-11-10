package com.synapse.rag.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${synapse.rag.topics.document-ingestion:document-ingestion-events}")
    private String ingestionTopic;

    @Value("${synapse.rag.topics.document-dlt:document-ingestion-events-dlt}")
    private String dltTopic;

    @Bean
    public NewTopic documentIngestionTopic() {
        return TopicBuilder.name(ingestionTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic documentDltTopic() {
        return TopicBuilder.name(dltTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
