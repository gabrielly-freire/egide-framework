package br.imd.ufrn.core.dto;

import java.time.LocalDateTime;

public record ServiceEvaluationResponse(
        Long id,
        Long manifestationId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
