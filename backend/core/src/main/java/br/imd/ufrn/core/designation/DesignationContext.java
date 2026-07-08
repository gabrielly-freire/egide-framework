package br.imd.ufrn.core.designation;

/**
 * Contexto passado à estratégia de designação de responsável.
 *
 * @param manifestationId   id da manifestação a ser designada
 * @param manifestationType tipo da manifestação (ex.: "DENUNCIA", "RECLAMACAO")
 * @param affectedRegion    região afetada declarada na manifestação (pode ser {@code null})
 */
public record DesignationContext(
        Long manifestationId,
        String manifestationType,
        String affectedRegion
) {}
