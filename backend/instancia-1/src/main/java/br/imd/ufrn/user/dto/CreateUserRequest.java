package br.imd.ufrn.user.dto;

import br.imd.ufrn.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotNull Role role,
        Long departmentId
) {}
