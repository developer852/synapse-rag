package com.synapse.rag.controller;

import com.synapse.rag.dto.DocumentIngestEvent;
import com.synapse.rag.dto.DocumentIngestRequest;
import com.synapse.rag.service.DocumentHasher;
import com.synapse.rag.service.VectorIndexingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Ingestion", description = "Asynchronous, decoupled document ingestion pipeline")
public class DocumentController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DocumentHasher hasher;
    private final VectorIndexingService indexingService;

    @Value("${synapse.rag.topics.document-ingestion:document-ingestion-events}")
    private String ingestionTopic;

    @PostMapping("/ingest")
    @Operation(summary = "Ingest Document Asynchronously", description = "Receives raw document, hashes content, and publishes to Kafka ingestion queue.")
    public ResponseEntity<Map<String, Object>> ingestDocument(@Valid @RequestBody DocumentIngestRequest request) {
        String contentHash = hasher.computeSha256(request.getContent());

        // Fast-path idempotency check
        if (indexingService.isAlreadyIndexed(contentHash)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "DUPLICATE_IGNORED",
                    "message", "Identical document content already indexed in the vector store",
                    "contentHash", contentHash
            ));
        }

        String documentId = "doc-" + UUID.randomUUID().toString().substring(0, 8);
        DocumentIngestEvent event = DocumentIngestEvent.builder()
                .documentId(documentId)
                .title(request.getTitle())
                .sourceType(request.getSourceType() != null ? request.getSourceType() : "USER_UPLOAD")
                .content(request.getContent())
                .contentHash(contentHash)
                .timestamp(Instant.now())
                .build();

        // Publish to Kafka
        kafkaTemplate.send(ingestionTopic, documentId, event);
        log.info("Dispatched document ingestion event to Kafka topic [{}]: {}", ingestionTopic, documentId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "status", "QUEUED",
                "documentId", documentId,
                "contentHash", contentHash,
                "message", "Document accepted for asynchronous indexing via Kafka worker pool."
        ));
    }
}
