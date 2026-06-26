package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.AuditEntry;
import br.imd.ufrn.core.dto.AuditEntryRequest;
import br.imd.ufrn.core.dto.AuditEntryResponse;
import br.imd.ufrn.core.exception.AuditEntryNotFoundException;
import br.imd.ufrn.core.mapper.AuditEntryMapper;
import br.imd.ufrn.core.persistence.AuditEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditEntryServiceImpl implements AuditEntryService {

    private final AuditEntryRepository repository;
    private final AuditEntryMapper mapper;

    @Override
    public AuditEntryResponse create(AuditEntryRequest request) {
        AuditEntry entry = new AuditEntry();
        entry.setManifestationId(request.manifestationId());
        entry.setActorId(request.actorId());
        entry.setAction(request.action());
        entry.setDescription(request.description());
        return mapper.toResponse(repository.save(entry));
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEntryResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AuditEntryNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEntryResponse> findAllByManifestationId(Long manifestationId, Pageable pageable) {
        return repository.findAllByManifestationId(manifestationId, pageable)
                .map(mapper::toResponse);
    }
}
