package br.imd.ufrn.service;

import br.imd.ufrn.dto.AccusationRequest;
import br.imd.ufrn.dto.AccusationResponse;

public interface AccusationService {

    /**
     * Registra (ou atualiza) o membro denunciado de uma manifestação. Há no máximo um denunciado
     * por manifestação: registrar novamente substitui o anterior.
     */
    AccusationResponse register(Long manifestationId, AccusationRequest request);

    AccusationResponse findByManifestationId(Long manifestationId);
}
