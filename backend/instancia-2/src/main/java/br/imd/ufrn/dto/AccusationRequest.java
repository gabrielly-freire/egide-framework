package br.imd.ufrn.dto;

import jakarta.validation.constraints.NotNull;

public record AccusationRequest(

        @NotNull(message = "O id do membro denunciado é obrigatório")
        Long accusedMemberId
) {}
