package br.imd.ufrn.core.service;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.core.designation.DesignationContext;
import br.imd.ufrn.core.designation.DesignationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import br.imd.ufrn.core.exception.AutoAssignmentUnavailableException;
import br.imd.ufrn.core.exception.ConflictOfInterestException;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final ManifestationRepository manifestationRepository;
    private final ResponsibleAssignmentService assignmentService;
    private final DesignationStrategy designationStrategy;
    private final ConflictOfInterestStrategy conflictOfInterestStrategy;

    @Override
    public ResponsibleAssignmentResponse autoAssign(Long manifestationId) {
        Manifestation manifestation = manifestationRepository.findById(manifestationId)
                .orElseThrow(() -> new ManifestationNotFoundException(manifestationId));

        DesignationContext designationContext = new DesignationContext(
                manifestationId, manifestation.getType());
        Long responsibleId = designationStrategy.resolve(designationContext);

        if (responsibleId == null) {
            throw new AutoAssignmentUnavailableException(manifestationId);
        }

        ConflictOfInterestContext conflictContext = new ConflictOfInterestContext(
                manifestationId, responsibleId, manifestation.getType());
        if (conflictOfInterestStrategy.hasConflict(conflictContext)) {
            throw new ConflictOfInterestException(responsibleId, manifestationId);
        }

        return assignmentService.assign(
                new ResponsibleAssignmentRequest(manifestationId, responsibleId, null));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConflict(Long manifestationId, Long analystId) {
        Manifestation manifestation = manifestationRepository.findById(manifestationId)
                .orElseThrow(() -> new ManifestationNotFoundException(manifestationId));

        ConflictOfInterestContext context = new ConflictOfInterestContext(
                manifestationId, analystId, manifestation.getType());
        return conflictOfInterestStrategy.hasConflict(context);
    }
}
