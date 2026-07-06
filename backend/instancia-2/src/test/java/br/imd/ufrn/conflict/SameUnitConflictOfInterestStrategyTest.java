package br.imd.ufrn.conflict;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.domain.AcademicMember;
import br.imd.ufrn.domain.ManifestationAccusation;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import br.imd.ufrn.persistence.ManifestationAccusationRepository;
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

    @Mock
    private ManifestationAccusationRepository accusationRepository;

    @InjectMocks
    private SameUnitConflictOfInterestStrategy strategy;

    private ConflictOfInterestContext context() {
        return new ConflictOfInterestContext(
                MANIFESTATION_ID, ANALYST_ID, "RECLAMACAO", java.util.List.of());
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

    private void givenAccused(String unit) {
        ManifestationAccusation accusation = new ManifestationAccusation();
        accusation.setManifestationId(MANIFESTATION_ID);
        accusation.setAccusedMemberId(ACCUSED_ID);
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(Optional.of(accusation));
        when(memberRepository.findById(ACCUSED_ID)).thenReturn(Optional.of(member(ACCUSED_ID, unit)));
    }

    @Test
    void hasConflict_deveRetornarTrue_quandoAnalistaEDenunciadoSaoDaMesmaUnidade() {
        givenAnalyst("DIMAP");
        givenAccused("DIMAP");

        assertThat(strategy.hasConflict(context())).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoUnidadesSaoDiferentes() {
        givenAnalyst("DCA");
        givenAccused("DIMAP");

        assertThat(strategy.hasConflict(context())).isFalse();
    }

    @Test
    void hasConflict_deveIgnorarCaixaNaComparacaoDeUnidade() {
        givenAnalyst("dimap");
        givenAccused("DIMAP");

        assertThat(strategy.hasConflict(context())).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoAnalistaNaoEstaCadastrado() {
        when(memberRepository.findById(ANALYST_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context())).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoManifestacaoNaoTemDenunciado() {
        givenAnalyst("DIMAP");
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context())).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoMembroDenunciadoNaoExiste() {
        givenAnalyst("DIMAP");
        ManifestationAccusation accusation = new ManifestationAccusation();
        accusation.setManifestationId(MANIFESTATION_ID);
        accusation.setAccusedMemberId(ACCUSED_ID);
        lenient().when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(Optional.of(accusation));
        when(memberRepository.findById(ACCUSED_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context())).isFalse();
    }
}
