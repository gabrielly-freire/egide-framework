package br.imd.ufrn.conflict;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.domain.ManifestationAccusation;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import br.imd.ufrn.persistence.ManifestationAccusationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Conflito de interesse da Instância 2 (Ouvidoria Universitária): o analista não pode pertencer à
 * mesma unidade acadêmica (centro ou departamento) do denunciado.
 *
 * <p>Resolve a unidade do analista (pelo {@code analystId} do contexto) e a do denunciado (via
 * {@link ManifestationAccusation} da manifestação) e compara, ignorando caixa. Há conflito quando
 * ambas as unidades são conhecidas e iguais.
 *
 * <p>Na ausência de dados — analista não cadastrado, manifestação sem denunciado registrado ou
 * denunciado inexistente — retorna {@code false}: sem informação não é possível <em>afirmar</em>
 * o impedimento, então a designação não é bloqueada.
 *
 * <p>Registrada como {@link Component}; desliga o default do Core
 * ({@code NoConflictOfInterestStrategy}) via {@code @ConditionalOnMissingBean}.
 */
@Component
@RequiredArgsConstructor
public class SameUnitConflictOfInterestStrategy implements ConflictOfInterestStrategy {

    private final AcademicMemberRepository memberRepository;
    private final ManifestationAccusationRepository accusationRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        Optional<AcademicMember> analyst = memberRepository.findById(context.analystId());
        if (analyst.isEmpty()) {
            return false;
        }

        Optional<AcademicMember> accused = accusationRepository
                .findByManifestationId(context.manifestationId())
                .flatMap(accusation -> memberRepository.findById(accusation.getAccusedMemberId()));
        if (accused.isEmpty()) {
            return false;
        }

        return analyst.get().getUnit().equalsIgnoreCase(accused.get().getUnit());
    }
}
