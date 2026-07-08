package br.imd.ufrn.core.dto;

import jakarta.validation.constraints.NotNull;

public record AccusationRequest(

        @NotNull(message = "O id da parte acusada é obrigatório")
        Long accusedPartyId
) {}
