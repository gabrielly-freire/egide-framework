package br.imd.ufrn.atendimento.dto;

import br.imd.ufrn.atendimento.domain.ImpedimentReason;
import jakarta.validation.constraints.NotNull;

public record LegalImpedimentRequest(

        @NotNull(message = "O id da manifestação é obrigatório")
        Long manifestationId,

        @NotNull(message = "O id do analista é obrigatório")
        Long analystId,

        @NotNull(message = "O motivo do impedimento é obrigatório")
        ImpedimentReason reason
) {}
