package br.imd.ufrn.core.dto;

public record ConflictCheckResponse(
        Long manifestationId,
        Long analystId,
        boolean hasConflict
) {}
