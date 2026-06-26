package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.ServiceEvaluation;
import br.imd.ufrn.core.dto.ServiceEvaluationRequest;
import br.imd.ufrn.core.dto.ServiceEvaluationResponse;
import br.imd.ufrn.core.exception.EvaluationAlreadyExistsException;
import br.imd.ufrn.core.exception.ServiceEvaluationNotFoundException;
import br.imd.ufrn.core.mapper.ServiceEvaluationMapper;
import br.imd.ufrn.core.persistence.ServiceEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceEvaluationServiceImpl implements ServiceEvaluationService {

    private final ServiceEvaluationRepository repository;
    private final ServiceEvaluationMapper mapper;

    @Override
    public ServiceEvaluationResponse create(ServiceEvaluationRequest request) {
        if (repository.existsByManifestationId(request.manifestationId())) {
            throw new EvaluationAlreadyExistsException(request.manifestationId());
        }
        ServiceEvaluation entity = mapper.toEntity(request);
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceEvaluationResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ServiceEvaluationNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceEvaluationResponse findByManifestationId(Long manifestationId) {
        return repository.findByManifestationId(manifestationId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ServiceEvaluationNotFoundException(manifestationId, true));
    }

    @Override
    public void delete(Long id) {
        ServiceEvaluation entity = repository.findById(id)
                .orElseThrow(() -> new ServiceEvaluationNotFoundException(id));
        entity.setActive(false);
        repository.save(entity);
    }
}
