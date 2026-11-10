package com.synapse.rag.controller;

import com.synapse.rag.dto.QueryRequest;
import com.synapse.rag.dto.QueryResponse;
import com.synapse.rag.service.RagQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/query")
@RequiredArgsConstructor
@Tag(name = "Semantic Search & RAG", description = "Query endpoint featuring Redis semantic caching and circuit breaker protection")
public class RagQueryController {

    private final RagQueryService queryService;

    @PostMapping
    @Operation(summary = "Ask question", description = "Checks Redis semantic cache first (<15ms). If miss, performs vector retrieval and guarded inference.")
    public ResponseEntity<QueryResponse> query(@Valid @RequestBody QueryRequest request) {
        QueryResponse response = queryService.executeQuery(request);
        return ResponseEntity.ok(response);
    }
}
