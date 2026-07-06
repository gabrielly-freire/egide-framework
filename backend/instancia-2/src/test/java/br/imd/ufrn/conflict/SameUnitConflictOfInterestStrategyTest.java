package br.imd.ufrn.conflict;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.domain.Party;
import br.imd.ufrn.core.persistence.PartyRepository;
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
    private PartyRepository partyRepository;

    @InjectMocks
    private SameUnitConflictOfInterestStrategy strategy;

    /** Contexto com os ids das partes acusadas já entregues pelo Core. */
    private ConflictOfInterestContext context(List<Long> accusedPartyIds) {
        return new ConflictOfInterestContext(MANIFESTATION_ID, ANALYST_ID, "RECLAMACAO", accusedPartyIds);
    }

    private Party party(Long id, String unit) {
        Party party = new Party();
        party.setId(id);
        party.setName("Parte " + id);
        party.setUnit(unit);
        return party;
    }

    private void givenAnalyst(String unit) {
        when(partyRepository.findById(ANALYST_ID)).thenReturn(Optional.of(party(ANALYST_ID, unit)));
    }

    private void givenParty(Long id, String unit) {
        when(partyRepository.findById(id)).thenReturn(Optional.of(party(id, unit)));
    }

    @Test
    void hasConflict_deveRetornarTrue_quandoAnalistaEDenunciadoSaoDaMesmaUnidade() {
        givenAnalyst("DIMAP");
        givenParty(ACCUSED_ID, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoUnidadesSaoDiferentes() {
        givenAnalyst("DCA");
        givenParty(ACCUSED_ID, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isFalse();
    }

    @Test
    void hasConflict_deveIgnorarCaixaNaComparacaoDeUnidade() {
        givenAnalyst("dimap");
        givenParty(ACCUSED_ID, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isTrue();
    }

    @Test
    void hasConflict_deveRetornarTrue_quandoAlgumDosAcusadosEDaMesmaUnidade() {
        givenAnalyst("DIMAP");
        givenParty(2L, "DCA");
        givenParty(3L, "DIMAP");

        assertThat(strategy.hasConflict(context(List.of(2L, 3L)))).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoAnalistaNaoEstaCadastrado() {
        when(partyRepository.findById(ANALYST_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoNaoHaAcusados() {
        givenAnalyst("DIMAP");

        assertThat(strategy.hasConflict(context(List.of()))).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoParteAcusadaNaoExiste() {
        givenAnalyst("DIMAP");
        when(partyRepository.findById(ACCUSED_ID)).thenReturn(Optional.empty());

        assertThat(strategy.hasConflict(context(List.of(ACCUSED_ID)))).isFalse();
    }
}
