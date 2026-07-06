package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.AccusationRequest;
import br.imd.ufrn.core.dto.AccusationResponse;
import java.util.List;

/**
 * Registro das partes acusadas de uma manifestação (ponto fixo de infraestrutura).
 * Fornece o dado que o ponto variável de conflito de interesse consome.
 */
public interface AccusationService {

    AccusationResponse register(Long manifestationId, AccusationRequest request);

    List<AccusationResponse> findByManifestationId(Long manifestationId);
}
