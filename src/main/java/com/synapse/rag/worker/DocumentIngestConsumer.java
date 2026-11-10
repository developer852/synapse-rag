package com.synapse.rag.worker;

import com.synapse.rag.dto.DocumentIngestEvent;
import com.synapse.rag.service.VectorIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentIngestConsumer {

    private final VectorIndexingService indexingService;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "${synapse.rag.topics.document-ingestion:document-ingestion-events}",
            groupId = "${spring.kafka.consumer.group-id:synapse-rag-group}"
    )
    public void consumeDocumentEvent(
            DocumentIngestEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String docKey,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received Ingestion Event -> ID: {}, Partition: {}, Offset: {}",
                event.getDocumentId(), partition, offset);

        // 1. Idempotency Check: Guard against duplicate Kafka event deliveries
        if (indexingService.isAlreadyIndexed(event.getContentHash())) {
            log.warn("Document [{}] with hash {} is already indexed. Skipping processing.",
                    event.getDocumentId(), event.getContentHash());
            return;
        }

        // 2. Validate poison pill condition
        if (event.getContent() == null || event.getContent().isBlank()) {
            throw new IllegalArgumentException("Cannot index document with empty or null content");
        }

        // 3. Process Chunking and Vector Indexing
        indexingService.chunkAndIndex(event);
        log.info("Document successfully indexed in knowledge vector store: {}", event.getDocumentId());
    }

    @DltHandler
    public void handleDeadLetterTopic(
            DocumentIngestEvent failedEvent,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String docKey,
            @Header(KafkaHeaders.ORIGINAL_TOPIC) String originalTopic) {

        log.error("CRITICAL: Message exhausted all 4 retry attempts. Shunted to DLT from topic: {}. Document ID: {}",
                originalTopic, failedEvent != null ? failedEvent.getDocumentId() : "UNKNOWN");
        // In production: trigger Slack notification or PagerDuty incident
    }
}
