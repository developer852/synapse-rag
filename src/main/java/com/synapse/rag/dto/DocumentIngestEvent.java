package com.synapse.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIngestEvent implements Serializable {

    private String documentId;
    private String title;
    private String sourceType;
    private String content;
    private String contentHash;
    private Instant timestamp;
}
