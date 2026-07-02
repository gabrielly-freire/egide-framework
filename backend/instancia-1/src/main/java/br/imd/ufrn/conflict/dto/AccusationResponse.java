package br.imd.ufrn.conflict.dto;

public record AccusationResponse(
        Long id,
        Long manifestationId,
        Long accusedUserId
) {}
