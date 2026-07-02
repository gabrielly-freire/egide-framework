package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.workflow.WorkflowStepResult;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final ManifestationRepository repository;
    private final ManifestationMapper mapper;
    private final WorkflowTemplate workflowTemplate;

    @Override
    public ManifestationResponse advance(Long manifestationId) {
        Manifestation manifestation = repository.findById(manifestationId)
                .orElseThrow(() -> new ManifestationNotFoundException(manifestationId));
        WorkflowStepResult result = workflowTemplate.advance(manifestation);
        manifestation.setStatus(result.nextStatus());
        manifestation.setDeadlineAt(toDeadline(result.deadline()));
        return mapper.toResponse(repository.save(manifestation));
    }

    @Override
    public ManifestationResponse appeal(Long manifestationId) {
        Manifestation manifestation = repository.findById(manifestationId)
                .orElseThrow(() -> new ManifestationNotFoundException(manifestationId));
        WorkflowStepResult result = workflowTemplate.appeal(manifestation);
        manifestation.setStatus(result.nextStatus());
        manifestation.setDeadlineAt(toDeadline(result.deadline()));
        return mapper.toResponse(repository.save(manifestation));
    }

    // Converte o prazo relativo da fase (Duration) em um instante absoluto; null se a fase não tem prazo.
    private LocalDateTime toDeadline(Duration duration) {
        return duration == null ? null : LocalDateTime.now().plus(duration);
    }
}
