package br.imd.ufrn.core.dto;

public record AccusationResponse(
        Long id,
        Long manifestationId,
        Long accusedPartyId
) {}
