package br.imd.ufrn.core.conflict;

/**
 * Contexto passado à estratégia de conflito de interesse.
 *
 * @param manifestationId id da manifestação sob análise
 * @param analystId       id do analista candidato à designação
 * @param manifestationType tipo da manifestação (ex.: "DENUNCIA", "RECLAMACAO")
 */
public record ConflictOfInterestContext(
        Long manifestationId,
        Long analystId,
        String manifestationType
) {}
