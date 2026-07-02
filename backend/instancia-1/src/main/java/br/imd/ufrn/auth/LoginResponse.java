package br.imd.ufrn.auth;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiresIn
) {}
