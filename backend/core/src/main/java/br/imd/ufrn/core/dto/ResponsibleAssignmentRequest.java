package br.imd.ufrn.core.dto;

import jakarta.validation.constraints.NotNull;

public record ResponsibleAssignmentRequest(

        @NotNull(message = "A manifestação é obrigatória")
        Long manifestationId,

        @NotNull(message = "O responsável é obrigatório")
        Long responsibleId,

        Long assignedById
) {}
