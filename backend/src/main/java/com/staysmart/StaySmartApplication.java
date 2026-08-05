package com.staysmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the StaySmart AI backend.
 *
 * <p>StaySmart AI is an Airbnb-inspired vacation rental platform with integrated AI
 * features (recommendations, review summarization, chat assistant, trip planning,
 * description generation, budget planning and smart search) built with Spring Boot,
 * Spring Security (JWT) and LangChain4j.</p>
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class StaySmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StaySmartApplication.class, args);
    }
}
