package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ServiceEvaluationRequest;
import br.imd.ufrn.core.dto.ServiceEvaluationResponse;

public interface ServiceEvaluationService {

    ServiceEvaluationResponse create(ServiceEvaluationRequest request);

    ServiceEvaluationResponse findById(Long id);

    ServiceEvaluationResponse findByManifestationId(Long manifestationId);

    void delete(Long id);
}
