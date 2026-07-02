package br.imd.ufrn.core.service;

import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationResult;
import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManifestationServiceImpl implements ManifestationService {

    private final ManifestationRepository repository;
    private final ManifestationMapper mapper;
    private final AnonymizationStrategy anonymizationStrategy;
    private final ApplicationEventPublisher eventPublisher;
    private final WorkflowTemplate workflowTemplate;

    @Override
    public ManifestationResponse create(ManifestationRequest request) {
        Manifestation entity = mapper.toEntity(request);
        entity.setProtocolNumber(generateProtocolNumber());
        entity.setStatus(ManifestationStatus.REGISTERED);
        entity.setActive(true);
        AnonymizationResult anonymized = anonymizationStrategy.anonymize(new AnonymizationContext(
                request.anonymous(), request.type(), request.title(), request.description()));
        entity.setTitle(anonymized.title());
        entity.setDescription(anonymized.description());
        Duration initialDeadline = workflowTemplate.initialDeadline();
        if (initialDeadline != null) {
            entity.setDeadlineAt(LocalDateTime.now().plus(initialDeadline));
        }
        Manifestation saved = repository.save(entity);
        eventPublisher.publishEvent(new ManifestationCreatedEvent(saved.getId()));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ManifestationResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ManifestationNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ManifestationResponse findByProtocolNumber(String protocolNumber) {
        return repository.findByProtocolNumber(protocolNumber)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ManifestationNotFoundException(protocolNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManifestationResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public ManifestationResponse update(Long id, ManifestationRequest request) {
        Manifestation entity = repository.findById(id)
                .orElseThrow(() -> new ManifestationNotFoundException(id));
        mapper.updateEntity(request, entity);
        AnonymizationResult anonymized = anonymizationStrategy.anonymize(new AnonymizationContext(
                request.anonymous(), request.type(), request.title(), request.description()));
        entity.setTitle(anonymized.title());
        entity.setDescription(anonymized.description());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Manifestation entity = repository.findById(id)
                .orElseThrow(() -> new ManifestationNotFoundException(id));
        entity.setActive(false);
        repository.save(entity);
    }

    private String generateProtocolNumber() {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return Year.now().getValue() + "-" + unique;
    }
}
