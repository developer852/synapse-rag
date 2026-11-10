package com.synapse.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableKafka
@EnableAsync
public class SynapseRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynapseRagApplication.class, args);
    }
}
