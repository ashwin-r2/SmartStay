package com.staysmart.ai.repository;

import com.staysmart.ai.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {
    List<AiChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
