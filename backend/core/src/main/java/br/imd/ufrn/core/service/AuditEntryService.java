package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.AuditEntryRequest;
import br.imd.ufrn.core.dto.AuditEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditEntryService {

    AuditEntryResponse create(AuditEntryRequest request);

    AuditEntryResponse findById(Long id);

    Page<AuditEntryResponse> findAllByManifestationId(Long manifestationId, Pageable pageable);
}
