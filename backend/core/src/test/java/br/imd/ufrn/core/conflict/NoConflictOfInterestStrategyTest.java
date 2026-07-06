package br.imd.ufrn.core.conflict;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class NoConflictOfInterestStrategyTest {

    private final ConflictOfInterestStrategy strategy = new NoConflictOfInterestStrategy();

    @Test
    void hasConflict_deveRetornarFalse_sempreQueSemConfiguracao() {
        ConflictOfInterestContext context =
                new ConflictOfInterestContext(1L, 10L, "DENUNCIA", List.of());

        assertThat(strategy.hasConflict(context)).isFalse();
    }

    @Test
    void hasConflict_deveRetornarFalse_paraDiferentesTipos() {
        assertThat(strategy.hasConflict(new ConflictOfInterestContext(1L, 10L, "RECLAMACAO", List.of()))).isFalse();
        assertThat(strategy.hasConflict(new ConflictOfInterestContext(2L, 20L, "SUGESTAO", List.of(2L)))).isFalse();
        assertThat(strategy.hasConflict(new ConflictOfInterestContext(3L, 30L, "ELOGIO", List.of()))).isFalse();
    }
}
