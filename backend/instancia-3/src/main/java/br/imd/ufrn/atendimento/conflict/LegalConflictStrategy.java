package br.imd.ufrn.atendimento.conflict;

import br.imd.ufrn.atendimento.persistence.LegalImpedimentRepository;
import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Impedimento legal nos moldes da LAI: um analista está impedido de atuar em uma
 * manifestação quando existe um {@link br.imd.ufrn.atendimento.domain.LegalImpediment}
 * cadastrado para o par manifestação/analista, motivado por citação ou parentesco
 * (ver {@link br.imd.ufrn.atendimento.domain.ImpedimentReason}). O cadastro é manual,
 * via {@link br.imd.ufrn.atendimento.web.LegalImpedimentController}; esta estratégia
 * apenas consulta a existência do registro.
 */
@Component
@RequiredArgsConstructor
public class LegalConflictStrategy implements ConflictOfInterestStrategy {

    private final LegalImpedimentRepository legalImpedimentRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        //TODO: ajustar para uma regra que faz mais sentido  para o dominio
        return legalImpedimentRepository.existsByManifestationIdAndAnalystId(
                context.manifestationId(), context.analystId());
    }
}
