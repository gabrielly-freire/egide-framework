package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ManifestationResponse;

public interface WorkflowService {

    ManifestationResponse advance(Long manifestationId);

    ManifestationResponse appeal(Long manifestationId);
}
