import matplotlib.pyplot as plt
import matplotlib.patches as patches
from PIL import Image

def create_diagram():
    fig, ax = plt.subplots(figsize=(16, 9), dpi=200)
    fig.patch.set_facecolor('#0b0f19')
    ax.set_facecolor('#0b0f19')
    ax.set_xlim(0, 16)
    ax.set_ylim(0, 9)
    ax.axis('off')

    # Title & Subtitle
    ax.text(8, 8.5, "SYNAPSE-RAG: ENTERPRISE EVENT-DRIVEN AI ARCHITECTURE", 
            fontsize=18, fontweight='bold', color='#ffffff', ha='center', va='center', fontfamily='sans-serif')
    ax.text(8, 8.15, "Java 21 • Spring Boot 3 • Apache Kafka • PostgreSQL (pgvector) • Redis Semantic Caching • Resilience4j", 
            fontsize=10, color='#94a3b8', ha='center', va='center', fontfamily='sans-serif')

    def draw_box(x, y, w, h, title, subtitle, badge="", border_color="#38bdf8", fill_color="#111827"):
        # Shadow
        rect_shadow = patches.FancyBboxPatch((x+0.05, y-0.05), w, h, boxstyle="round,pad=0.15,rounding_size=0.15",
                                             facecolor='#030712', edgecolor='none', alpha=0.6)
        ax.add_patch(rect_shadow)
        # Main box
        rect = patches.FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.15,rounding_size=0.15",
                                      facecolor=fill_color, edgecolor=border_color, linewidth=1.8)
        ax.add_patch(rect)
        if badge:
            badge_box = patches.FancyBboxPatch((x + 0.15, y + h - 0.2), len(badge)*0.11 + 0.2, 0.25,
                                               boxstyle="round,pad=0.04,rounding_size=0.08",
                                               facecolor=border_color, edgecolor='none')
            ax.add_patch(badge_box)
            ax.text(x + 0.25, y + h - 0.08, badge, fontsize=7.5, fontweight='bold', color='#0b0f19')
            
        ax.text(x + w/2, y + h*0.58, title, fontsize=11, fontweight='bold', color='#f8fafc', ha='center', va='center')
        ax.text(x + w/2, y + h*0.28, subtitle, fontsize=8, color='#94a3b8', ha='center', va='center')

    # --- TOP ROW: Ingestion Flow (Decoupled & Event-Driven) ---
    ax.text(1.2, 7.5, "1. INGESTION PIPELINE (Asynchronous)", fontsize=11, fontweight='bold', color='#38bdf8')

    draw_box(1.0, 5.8, 2.8, 1.2, "Client / Doc Uploader", "PDF, Docs, Knowledge Text", badge="HTTP REST", border_color="#38bdf8")
    draw_box(4.5, 5.8, 3.2, 1.2, "Ingestion Service", "SHA-256 Hasher & Fast Idempotency", badge="Spring Boot 3", border_color="#38bdf8")
    draw_box(8.5, 5.8, 3.2, 1.2, "Kafka Broker", "Topic: doc-ingestion-events\nPartitions: 3 | Replica: 1", badge="KRaft Stream", border_color="#f59e0b")
    draw_box(12.4, 5.8, 2.8, 1.2, "DLT (Dead Letter)", "4x Retries • Exp Backoff\nPoison Pill Quarantine", badge="Kafka DLT", border_color="#ef4444", fill_color="#1e1014")

    # --- MIDDLE ROW: Worker & Storage ---
    ax.text(1.2, 4.8, "2. WORKER & STORAGE (HNSW Dense Vector Indexing)", fontsize=11, fontweight='bold', color='#a855f7')

    draw_box(8.5, 3.2, 3.2, 1.2, "Worker Consumer Pool", "@RetryableTopic • Token-aware Chunker\nIdempotent DB Check", badge="Spring Kafka", border_color="#a855f7")
    draw_box(12.4, 3.2, 2.8, 1.2, "PostgreSQL + pgvector", "HNSW Index (m=16, ef=64)\nDense Vector Similarity", badge="Database Layer", border_color="#a855f7")

    # --- BOTTOM ROW: Query & Inference Engine with Semantic Caching ---
    ax.text(1.2, 2.4, "3. QUERY PIPELINE (Sub-15ms Semantic Caching + Resilience)", fontsize=11, fontweight='bold', color='#34d399')

    draw_box(1.0, 0.7, 2.8, 1.2, "End User Query", "\"What is our SLA policy?\"\nNatural Language Prompt", badge="User Query", border_color="#34d399")
    draw_box(4.5, 0.7, 3.2, 1.2, "Redis Semantic Cache", "Cosine Proximity Match\n⚡ < 15ms Cache Hit!", badge="Redis Stack", border_color="#10b981", fill_color="#062e24")
    draw_box(8.5, 0.7, 3.2, 1.2, "Vector Context Search", "Top-K Context Retrieval\nSimilarity Threshold > 0.75", badge="pgvector Query", border_color="#34d399")
    draw_box(12.4, 0.7, 2.8, 1.2, "LLM Inference Engine", "Resilience4j Circuit Breaker\nFallback on HTTP 429", badge="GenAI Model", border_color="#f43f5e")

    # --- Connectors / Arrows ---
    def draw_arrow(x1, y1, x2, y2, color="#64748b", label=""):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle="-|>", color=color, lw=1.8, mutation_scale=14))
        if label:
            mx, my = (x1 + x2)/2, (y1 + y2)/2 + 0.15
            ax.text(mx, my, label, fontsize=7.5, color=color, ha='center', fontweight='bold')

    # Ingestion arrows
    draw_arrow(3.8, 6.4, 4.5, 6.4, color="#38bdf8", label="POST /ingest")
    draw_arrow(7.7, 6.4, 8.5, 6.4, color="#38bdf8", label="Async Event")
    draw_arrow(11.7, 6.4, 12.4, 6.4, color="#ef4444", label="On 4th Failure")

    # Kafka to Worker
    draw_arrow(10.1, 5.8, 10.1, 4.4, color="#a855f7", label="Consume")
    # Worker to PGVector
    draw_arrow(11.7, 3.8, 12.4, 3.8, color="#a855f7", label="Batch Embeddings")

    # Query arrows
    draw_arrow(3.8, 1.3, 4.5, 1.3, color="#34d399", label="POST /query")
    draw_arrow(7.7, 1.3, 8.5, 1.3, color="#34d399", label="Cache Miss")
    draw_arrow(10.1, 1.9, 13.8, 3.2, color="#a855f7", label="Retrieve Top-K")
    draw_arrow(11.7, 1.3, 12.4, 1.3, color="#f43f5e", label="Prompt + Context")
    
    # Return cache hit back
    ax.annotate('', xy=(2.4, 1.9), xytext=(5.5, 1.9),
                arrowprops=dict(arrowstyle="-|>", color="#10b981", lw=1.6, linestyle='dashed', mutation_scale=12))
    ax.text(3.9, 2.05, "Cache Hit Return (<15ms)", fontsize=7.5, color="#10b981", ha='center', fontweight='bold')

    plt.tight_layout()
    output_path = r"C:\Users\devik\.gemini\antigravity\scratch\synapse-rag\synapse-rag-architecture.png"
    plt.savefig(output_path, facecolor=fig.get_facecolor(), edgecolor='none', bbox_inches='tight')
    plt.close()
    print("Architecture diagram generated at:", output_path)

if __name__ == '__main__':
    create_diagram()
