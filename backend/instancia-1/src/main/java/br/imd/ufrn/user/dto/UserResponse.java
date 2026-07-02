package br.imd.ufrn.user.dto;

import br.imd.ufrn.user.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        String username,
        Role role,
        Long departmentId,
        String departmentName
) {}
