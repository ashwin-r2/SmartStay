package com.staysmart.ai.dto;

import com.staysmart.ai.entity.AiChatMessage;

import java.time.Instant;

public record ChatMessageDto(String role, String content, Instant createdAt) {
    public static ChatMessageDto from(AiChatMessage m) {
        return new ChatMessageDto(m.getRole().name(), m.getContent(), m.getCreatedAt());
    }
}
