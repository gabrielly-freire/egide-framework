package br.imd.ufrn.atendimento.dto;

import java.time.LocalDateTime;

public record AnalystResponse(
        Long id,
        String name,
        String specialty,
        String region,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
