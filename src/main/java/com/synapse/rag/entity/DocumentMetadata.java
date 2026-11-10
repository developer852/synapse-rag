package com.synapse.rag.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "document_metadata", indexes = {
        @Index(name = "idx_doc_content_hash", columnList = "content_hash", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadata {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @Column(name = "content_hash", nullable = false, unique = true, length = 64)
    private String contentHash;

    @Column(name = "total_chunks")
    private Integer totalChunks;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private IngestionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum IngestionStatus {
        PENDING,
        INDEXED,
        FAILED
    }
}
