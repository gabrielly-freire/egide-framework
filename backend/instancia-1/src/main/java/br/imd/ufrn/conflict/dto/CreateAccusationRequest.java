package br.imd.ufrn.conflict.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAccusationRequest(
        @NotNull Long accusedUserId
) {}
