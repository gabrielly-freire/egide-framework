package br.imd.ufrn.dto;

public record AccusationResponse(
        Long id,
        Long manifestationId,
        Long accusedMemberId
) {}
