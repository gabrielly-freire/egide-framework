package br.imd.ufrn.conflict;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.core.domain.Party;
import br.imd.ufrn.core.persistence.PartyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Conflito de interesse da Instância 2 (Ouvidoria Universitária): o analista não pode pertencer à
 * mesma unidade acadêmica (centro ou departamento) de nenhuma das partes acusadas.
 *
 * <p>Toda a infraestrutura vem do Core: as partes acusadas chegam prontas no contexto
 * ({@link ConflictOfInterestContext#accusedPartyIds()}) e o cadastro de pessoas/unidades é o
 * registro genérico {@link Party} do Core ({@code /v1/parties}). A instância <em>não implementa</em>
 * entidade, repositório, serviço ou controller próprios — apenas <b>consome</b> a persistência do
 * Core e aplica a sua regra (comparar unidades).
 *
 * <p>Há conflito quando o analista está cadastrado e sua unidade coincide (ignorando caixa) com a
 * de alguma parte acusada. Sem dados suficientes retorna {@code false}: não é possível
 * <em>afirmar</em> o impedimento.
 *
 * <p>Registrada como {@link Component}; desliga o default do Core
 * ({@code NoConflictOfInterestStrategy}) via {@code @ConditionalOnMissingBean}.
 */
@Component
@RequiredArgsConstructor
public class SameUnitConflictOfInterestStrategy implements ConflictOfInterestStrategy {

    private final PartyRepository partyRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        Optional<Party> analyst = partyRepository.findById(context.analystId());
        if (analyst.isEmpty()) {
            return false;
        }
        String analystUnit = analyst.get().getUnit();

        return context.accusedPartyIds().stream()
                .map(partyRepository::findById)
                .flatMap(Optional::stream)
                .anyMatch(accused -> analystUnit.equalsIgnoreCase(accused.getUnit()));
    }
}
