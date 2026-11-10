package com.synapse.rag.repository;

import com.synapse.rag.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, String> {

    Optional<DocumentMetadata> findByContentHash(String contentHash);

    boolean existsByContentHash(String contentHash);
}
