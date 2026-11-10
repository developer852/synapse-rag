package com.synapse.rag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIngestRequest {

    @NotBlank(message = "Document title cannot be blank")
    private String title;

    private String sourceType;

    @NotBlank(message = "Document content cannot be blank")
    private String content;
}
