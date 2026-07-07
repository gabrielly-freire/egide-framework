package br.imd.ufrn.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Papéis dos atores da Instância 1 (Compliance). Usados como autoridade de acesso
 * ({@code ROLE_<name>}) nos {@code @PreAuthorize}. Portado do monolito original.
 */
@AllArgsConstructor
@Getter
public enum Role {
    REMONSTRANT("Reclamante"),
    LISTENER("Ouvidor"),
    GENERAL_LISTENER("Ouvidor Geral"),
    MANAGER("Gestor"),
    ADMIN("Administrador");

    private final String description;
}
