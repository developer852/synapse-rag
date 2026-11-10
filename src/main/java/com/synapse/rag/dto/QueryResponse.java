package com.synapse.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    private String answer;
    private boolean cacheHit;
    private long latencyMs;
    private List<String> sourceSnippets;
}
