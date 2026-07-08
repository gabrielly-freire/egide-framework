package br.imd.ufrn.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Papéis dos atores da Instância 1 (Compliance). Usados como autoridade de acesso
 * ({@code ROLE_<name>}) nos {@code @PreAuthorize}.
 *
 * <p>{@code level} representa a posição na hierarquia (maior = mais alto), conforme o monolito
 * original: {@code ADMIN > GENERAL_LISTENER > MANAGER > LISTENER > REMONSTRANT}. É usado nas regras
 * de negócio (ex.: conflito de interesse por cargo) em vez de {@code ordinal()}, que depende da
 * ordem de declaração.
 */
@AllArgsConstructor
@Getter
public enum Role {
    REMONSTRANT("Reclamante", 0),
    LISTENER("Ouvidor", 1),
    MANAGER("Gestor", 2),
    GENERAL_LISTENER("Ouvidor Geral", 3),
    ADMIN("Administrador", 4);

    private final String description;
    private final int level;
}
