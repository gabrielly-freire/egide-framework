package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.ResponsibleAssignment;
import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import br.imd.ufrn.core.exception.ResponsibleAssignmentNotFoundException;
import br.imd.ufrn.core.mapper.ResponsibleAssignmentMapper;
import br.imd.ufrn.core.persistence.ResponsibleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ResponsibleAssignmentServiceImpl implements ResponsibleAssignmentService {

    private final ResponsibleAssignmentRepository repository;
    private final ResponsibleAssignmentMapper mapper;

    @Override
    public ResponsibleAssignmentResponse assign(ResponsibleAssignmentRequest request) {
        repository.findByManifestationId(request.manifestationId()).ifPresent(existing -> {
            existing.setActive(false);
            repository.save(existing);
        });

        ResponsibleAssignment entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsibleAssignmentResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponsibleAssignmentNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsibleAssignmentResponse findByManifestationId(Long manifestationId) {
        return repository.findByManifestationId(manifestationId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponsibleAssignmentNotFoundException(manifestationId, true));
    }

    @Override
    public void unassign(Long manifestationId) {
        ResponsibleAssignment entity = repository.findByManifestationId(manifestationId)
                .orElseThrow(() -> new ResponsibleAssignmentNotFoundException(manifestationId, true));
        entity.setActive(false);
        repository.save(entity);
    }
}
