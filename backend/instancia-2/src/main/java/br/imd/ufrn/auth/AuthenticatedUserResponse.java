package br.imd.ufrn.auth;

import br.imd.ufrn.user.Role;

public record AuthenticatedUserResponse(
        Long id,
        String name,
        String email,
        String username,
        Role role
) {}
