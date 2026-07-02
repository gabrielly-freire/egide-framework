package br.imd.ufrn.atendimento.conflict;

import br.imd.ufrn.atendimento.persistence.LegalImpedimentRepository;
import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegalConflictStrategy implements ConflictOfInterestStrategy {

    private final LegalImpedimentRepository legalImpedimentRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        return legalImpedimentRepository.existsByManifestationIdAndAnalystId(
                context.manifestationId(), context.analystId());
    }
}
