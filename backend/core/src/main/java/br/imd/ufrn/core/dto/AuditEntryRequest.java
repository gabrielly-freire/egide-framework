package br.imd.ufrn.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuditEntryRequest(

        @NotNull(message = "A manifestação é obrigatória")
        Long manifestationId,

        @NotNull(message = "O ator é obrigatório")
        Long actorId,

        @NotBlank(message = "A ação é obrigatória")
        String action,

        String description
) {}
