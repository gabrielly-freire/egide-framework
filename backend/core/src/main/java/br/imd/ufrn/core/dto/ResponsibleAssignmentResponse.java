package br.imd.ufrn.core.dto;

import java.time.LocalDateTime;

public record ResponsibleAssignmentResponse(
        Long id,
        Long manifestationId,
        Long responsibleId,
        Long assignedById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
