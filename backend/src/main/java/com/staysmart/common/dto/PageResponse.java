package com.staysmart.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Lightweight, frontend-friendly wrapper around Spring's {@link Page} that avoids leaking
 * internal Spring paging classes through the REST API.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
