package br.imd.ufrn.core.dto;

import br.imd.ufrn.core.domain.DecisionType;
import java.time.LocalDateTime;

public record DecisionRecordResponse(
        Long id,
        Long manifestationId,
        Long authorId,
        DecisionType type,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
