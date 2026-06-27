package br.imd.ufrn.atendimento.designation;

import br.imd.ufrn.atendimento.persistence.AnalystRepository;
import br.imd.ufrn.core.designation.DesignationContext;
import br.imd.ufrn.core.designation.DesignationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionDesignationStrategy implements DesignationStrategy {

    private final AnalystRepository analystRepository;

    @Override
    public Long resolve(DesignationContext context) {
        return analystRepository.findFirstBySpecialty(context.manifestationType())
                .map(analyst -> analyst.getId())
                .orElse(null);
    }
}
