package br.imd.ufrn.atendimento.service;

import br.imd.ufrn.atendimento.dto.LegalImpedimentRequest;
import br.imd.ufrn.atendimento.dto.LegalImpedimentResponse;
import java.util.List;

public interface LegalImpedimentService {

    LegalImpedimentResponse register(LegalImpedimentRequest request);

    LegalImpedimentResponse findById(Long id);

    List<LegalImpedimentResponse> findByManifestationId(Long manifestationId);

    void remove(Long id);
}
