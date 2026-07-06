package br.imd.ufrn.conflict;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SameUnitConflictOfInterestStrategyTest {

    private static final Long MANIFESTATION_ID = 10L;
    private static final Long ANALYST_ID = 1L;
    private static final Long ACCUSED_ID = 2L;

    @Mock
    private AcademicMemberRepository memberRepository;

    @InjectMocks
    private SameUnitConflictOfInterestStrategy strategy;

    /** Contexto com os ids das partes acusadas já entregues pelo Core. */
    private ConflictOfInterestContext context(List<Long> accusedPartyIds) {
        return new ConflictOfInterestContext(MANIFESTATION_ID, ANALYST_ID, "RECLAMACAO", accusedPartyIds);
    }

    private AcademicMember member(Long id, String unit) {
        AcademicMember member = new AcademicMember();
        member.setId(id);
        member.setName("Membro " + id);
        member.setUnit(unit);
        return member;
    }

    private void givenAnalyst(String unit) {
        when(memberRepository.findById(ANALYST_ID)).thenReturn(Optional.of(member(ANALYST_ID, unit)));
    }

    private void givenAccused(Long id, String unit) {
        when(memberRepository.findById(id)).thenReturn(Optional.of(member(id, unit)));
    }

    @Test
    void hasConflict_deveRetornarTrue_quandoAnalistaEDenunciadoSaoDaMesmaUnidade() {
        givenAnalyst("DIMAP");
        givenAccused(ACCUSED_ID, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoUnidadesSaoDiferentes() {
        givenAnalyst("DCA");
        givenAccused(ACCUSED_ID, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isFalse();
    }

    @Test
    void hasConflict_deveIgnorarCaixaNaComparacaoDeUnidade() {
        givenAnalyst("dimap");
        givenAccused(ACCUSED_ID, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isTrue();
    }

    @Test
    void hasConflict_deveRetornarTrue_quandoAlgumDosAcusadosEDaMesmaUnidade() {
        givenAnalyst("DIMAP");
        givenAccused(2L, "DCA");
        givenAccused(3L, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(2L, 3L)))).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoAnalistaNaoEstaCadastrado() {
        when(memberRepository.findById(ANALYST_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoNaoHaAcusados() {
        givenAnalyst("DIMAP");

        assertThat(strategy.hasConflict(context(List.of()))).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoMembroAcusadoNaoExiste() {
        givenAnalyst("DIMAP");
        when(memberRepository.findById(ACCUSED_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isFalse();
    }
}
