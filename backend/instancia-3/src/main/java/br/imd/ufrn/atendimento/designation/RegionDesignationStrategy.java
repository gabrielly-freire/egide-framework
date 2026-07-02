package br.imd.ufrn.atendimento.designation;

import br.imd.ufrn.atendimento.domain.Analyst;
import br.imd.ufrn.atendimento.persistence.AnalystRepository;
import br.imd.ufrn.core.designation.DesignationContext;
import br.imd.ufrn.core.designation.DesignationStrategy;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Designa o analista responsável por especialidade do órgão e, quando nenhum analista da
 * especialidade estiver disponível, cai para a região afetada declarada na manifestação.
 */
@Component
@RequiredArgsConstructor
public class RegionDesignationStrategy implements DesignationStrategy {

    private final AnalystRepository analystRepository;

    @Override
    public Long resolve(DesignationContext context) {
        Optional<Analyst> bySpecialty = analystRepository.findFirstBySpecialty(context.manifestationType());
        if (bySpecialty.isPresent()) {
            return bySpecialty.get().getId();
        }

        if (context.affectedRegion() == null) {
            return null;
        }

        return analystRepository.findFirstByRegion(context.affectedRegion())
                .map(Analyst::getId)
                .orElse(null);
    }
}
