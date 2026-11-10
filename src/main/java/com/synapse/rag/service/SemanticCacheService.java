package com.synapse.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DocumentHasher hasher;

    @Value("${synapse.rag.semantic-cache.enabled:true}")
    private boolean cacheEnabled;

    @Value("${synapse.rag.semantic-cache.ttl-seconds:3600}")
    private long cacheTtlSeconds;

    private static final String CACHE_KEY_PREFIX = "synapse:rag:cache:";

    public Optional<String> get(String normalizedQuery) {
        if (!cacheEnabled) {
            return Optional.empty();
        }

        String cacheKey = buildCacheKey(normalizedQuery);
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            log.info("⚡ Semantic Cache HIT for key: {}", cacheKey);
            return Optional.of(cachedValue.toString());
        }

        log.debug("Semantic Cache MISS for key: {}", cacheKey);
        return Optional.empty();
    }

    public void put(String normalizedQuery, String answer) {
        if (!cacheEnabled) {
            return;
        }

        String cacheKey = buildCacheKey(normalizedQuery);
        redisTemplate.opsForValue().set(cacheKey, answer, Duration.ofSeconds(cacheTtlSeconds));
        log.info("Saved response to Semantic Cache with TTL {}s [Key: {}]", cacheTtlSeconds, cacheKey);
    }

    private String buildCacheKey(String query) {
        // Clean and normalize query whitespace and punctuation
        String cleaned = query.trim().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
        return CACHE_KEY_PREFIX + hasher.computeSha256(cleaned);
    }
}
