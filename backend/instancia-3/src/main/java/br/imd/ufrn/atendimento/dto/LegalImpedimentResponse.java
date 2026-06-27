package br.imd.ufrn.atendimento.dto;

import br.imd.ufrn.atendimento.domain.ImpedimentReason;
import java.time.LocalDateTime;

public record LegalImpedimentResponse(
        Long id,
        Long manifestationId,
        Long analystId,
        ImpedimentReason reason,
        LocalDateTime createdAt
) {}
