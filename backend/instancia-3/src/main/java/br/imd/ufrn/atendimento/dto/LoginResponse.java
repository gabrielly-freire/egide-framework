package br.imd.ufrn.atendimento.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiresIn
) {}
