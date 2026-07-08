package br.imd.ufrn.core.designation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ManualDesignationStrategyTest {

    private final DesignationStrategy strategy = new ManualDesignationStrategy();

    @Test
    void resolve_deveRetornarNull_indicandoDesignacaoManual() {
        DesignationContext context = new DesignationContext(1L, "DENUNCIA", null);

        assertThat(strategy.resolve(context)).isNull();
    }

    @Test
    void resolve_deveRetornarNull_paraDiferentesTipos() {
        assertThat(strategy.resolve(new DesignationContext(1L, "RECLAMACAO", null))).isNull();
        assertThat(strategy.resolve(new DesignationContext(2L, "SUGESTAO", "Sul"))).isNull();
    }
}
