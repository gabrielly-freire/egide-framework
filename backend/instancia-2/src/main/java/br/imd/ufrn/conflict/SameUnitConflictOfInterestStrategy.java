package br.imd.ufrn.conflict;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Conflito de interesse da Instância 2 (Ouvidoria Universitária): o analista não pode pertencer à
 * mesma unidade acadêmica (centro ou departamento) de nenhuma das partes acusadas.
 *
 * <p>As partes acusadas vêm <b>prontas no contexto</b> ({@link ConflictOfInterestContext#accusedPartyIds()}),
 * carregadas pelo Core a partir do registro genérico de acusações — a instância <em>não</em>
 * precisa de repositório/serviço/controller próprios para isso. Aqui os ids são interpretados como
 * {@link AcademicMember}: a estratégia apenas resolve as unidades e compara.
 *
 * <p>Há conflito quando o analista está cadastrado e sua unidade coincide (ignorando caixa) com a
 * de alguma parte acusada. Sem dados suficientes — analista não cadastrado, sem acusados, ou
 * acusado inexistente — retorna {@code false}: não é possível <em>afirmar</em> o impedimento.
 *
 * <p>Registrada como {@link Component}; desliga o default do Core
 * ({@code NoConflictOfInterestStrategy}) via {@code @ConditionalOnMissingBean}.
 */
@Component
@RequiredArgsConstructor
public class SameUnitConflictOfInterestStrategy implements ConflictOfInterestStrategy {

    private final AcademicMemberRepository memberRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        Optional<AcademicMember> analyst = memberRepository.findById(context.analystId());
        if (analyst.isEmpty()) {
            return false;
        }
        String analystUnit = analyst.get().getUnit();

        return context.accusedPartyIds().stream()
                .map(memberRepository::findById)
                .flatMap(Optional::stream)
                .anyMatch(accused -> analystUnit.equalsIgnoreCase(accused.getUnit()));
    }
}
