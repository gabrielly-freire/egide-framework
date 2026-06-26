package br.imd.ufrn.core.dto;

import java.time.LocalDateTime;

public record AuditEntryResponse(
        Long id,
        Long manifestationId,
        Long actorId,
        String action,
        String description,
        LocalDateTime occurredAt
) {}
