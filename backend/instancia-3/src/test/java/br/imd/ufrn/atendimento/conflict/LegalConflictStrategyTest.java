package br.imd.ufrn.atendimento.conflict;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.atendimento.persistence.LegalImpedimentRepository;
import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegalConflictStrategyTest {

    @Mock
    private LegalImpedimentRepository repository;

    @InjectMocks
    private LegalConflictStrategy strategy;

    @Test
    void hasConflict_deveRetornarTrue_quandoImpedimentoExiste() {
        ConflictOfInterestContext context = new ConflictOfInterestContext(1L, 2L, "SAUDE");
        when(repository.existsByManifestationIdAndAnalystId(1L, 2L)).thenReturn(true);

        assertThat(strategy.hasConflict(context)).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoNaoHaImpedimento() {
        ConflictOfInterestContext context = new ConflictOfInterestContext(1L, 2L, "SAUDE");
        when(repository.existsByManifestationIdAndAnalystId(1L, 2L)).thenReturn(false);

        assertThat(strategy.hasConflict(context)).isFalse();
    }
}
