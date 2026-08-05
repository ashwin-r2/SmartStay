package com.staysmart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the full Spring context (security, JPA, JWT, LangChain4j wiring,
 * scheduling) starts successfully against the H2 test database with no OpenAI API key set.
 */
@SpringBootTest
@ActiveProfiles("test")
class StaySmartApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test fails with the root cause.
    }
}
