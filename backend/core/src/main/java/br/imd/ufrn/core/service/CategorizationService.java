package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ManifestationResponse;

/**
 * Aplica a categorização de uma manifestação delegando à
 * {@link br.imd.ufrn.core.categorization.CategorizationStrategy} configurada e persistindo o
 * resultado (categoria e risco) na própria manifestação.
 *
 * <p>É idempotente: pode ser reexecutado para reclassificar uma manifestação já registrada.
 */
public interface CategorizationService {

    ManifestationResponse categorize(Long manifestationId);
}
