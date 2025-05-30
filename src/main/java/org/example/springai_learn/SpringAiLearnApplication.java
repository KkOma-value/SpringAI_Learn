package org.example.springai_learn;


import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class SpringAiLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiLearnApplication.class, args);
    }

}
