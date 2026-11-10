-- Enable vector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Document Metadata Table (Tracks ingestion status and prevents duplicates)
CREATE TABLE IF NOT EXISTS document_metadata (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    source_type VARCHAR(50),
    content_hash VARCHAR(64) NOT NULL UNIQUE,
    total_chunks INT DEFAULT 0,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Vector Chunks Table with 1536-dim vector for embeddings (standard OpenAI/Gemini/BERT)
CREATE TABLE IF NOT EXISTS vector_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id VARCHAR(64) REFERENCES document_metadata(id) ON DELETE CASCADE,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    embedding vector(384), -- 384-dim for lightweight sentence-transformers / embeddings
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create HNSW Index for ultra-fast approximate nearest neighbor search
CREATE INDEX IF NOT EXISTS idx_chunks_embedding_hnsw 
ON vector_chunks 
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);
