package com.synapse.rag.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Synapse-RAG: Event-Driven AI Knowledge Engine")
                        .version("1.0.0")
                        .description("High-throughput RAG microservice with asynchronous Kafka ingestion, pgvector search, and Redis semantic caching.")
                        .contact(new Contact().name("Backend Engineering Team").email("engineering@synapse.ai")));
    }
}
