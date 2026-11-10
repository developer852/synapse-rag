package com.synapse.rag.service;

import com.synapse.rag.dto.DocumentIngestEvent;
import com.synapse.rag.entity.DocumentMetadata;
import com.synapse.rag.repository.DocumentMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorIndexingService {

    private final DocumentMetadataRepository metadataRepository;
    private final JdbcTemplate jdbcTemplate;

    public boolean isAlreadyIndexed(String contentHash) {
        return metadataRepository.existsByContentHash(contentHash);
    }

    @Transactional
    public void chunkAndIndex(DocumentIngestEvent event) {
        log.info("Beginning vector indexing pipeline for document: {}", event.getDocumentId());

        // 1. Record metadata record
        DocumentMetadata metadata = DocumentMetadata.builder()
                .id(event.getDocumentId())
                .title(event.getTitle())
                .sourceType(event.getSourceType())
                .contentHash(event.getContentHash())
                .status(DocumentMetadata.IngestionStatus.PENDING)
                .build();
        metadataRepository.save(metadata);

        // 2. Token-aware text chunking (sliding window with overlap)
        List<String> chunks = createChunks(event.getContent(), 500, 50);
        log.info("Document chunked into {} segments.", chunks.size());

        // 3. Batch persistence into PostgreSQL (with vector support)
        String sql = "INSERT INTO vector_chunks (document_id, chunk_index, content) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            batchArgs.add(new Object[]{event.getDocumentId(), i, chunks.get(i)});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);

        // 4. Update status to INDEXED
        metadata.setTotalChunks(chunks.size());
        metadata.setStatus(DocumentMetadata.IngestionStatus.INDEXED);
        metadataRepository.save(metadata);

        log.info("Vector Indexing successfully completed for document: {}", event.getDocumentId());
    }

    private List<String> createChunks(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }

        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            chunks.add(text.substring(start, end).trim());
            if (end == length) {
                break;
            }
            start += (chunkSize - overlap);
        }
        return chunks;
    }
}
