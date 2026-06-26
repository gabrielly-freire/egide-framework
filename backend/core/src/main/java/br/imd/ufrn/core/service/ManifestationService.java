package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManifestationService {

    ManifestationResponse create(ManifestationRequest request);

    ManifestationResponse findById(Long id);

    ManifestationResponse findByProtocolNumber(String protocolNumber);

    Page<ManifestationResponse> findAll(Pageable pageable);

    ManifestationResponse update(Long id, ManifestationRequest request);

    void delete(Long id);
}
