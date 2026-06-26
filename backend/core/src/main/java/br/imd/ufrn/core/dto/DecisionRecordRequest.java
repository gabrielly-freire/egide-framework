package br.imd.ufrn.core.dto;

import br.imd.ufrn.core.domain.DecisionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DecisionRecordRequest(

        @NotNull(message = "A manifestação é obrigatória")
        Long manifestationId,

        @NotNull(message = "O autor é obrigatório")
        Long authorId,

        @NotNull(message = "O tipo é obrigatório")
        DecisionType type,

        @NotBlank(message = "O conteúdo é obrigatório")
        String content
) {}
