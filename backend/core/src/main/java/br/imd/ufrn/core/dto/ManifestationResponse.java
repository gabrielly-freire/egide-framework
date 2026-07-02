package br.imd.ufrn.core.dto;

import br.imd.ufrn.core.domain.ManifestationStatus;
import java.time.LocalDateTime;

public record ManifestationResponse(
        Long id,
        String protocolNumber,
        String title,
        String description,
        String type,
        ManifestationStatus status,
        String category,
        String riskLevel,
        String affectedRegion,
        Integer appealCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
