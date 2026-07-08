package br.imd.ufrn.atendimento.categorization;

import static org.assertj.core.api.Assertions.assertThat;

import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import org.junit.jupiter.api.Test;

class PublicServiceCategorizationStrategyTest {

    private final PublicServiceCategorizationStrategy strategy = new PublicServiceCategorizationStrategy();

    @Test
    void categorize_deveRetornarTipoComoCategoria_quandoTipoInformado() {
        CategorizationContext context = new CategorizationContext(1L, "Título", "Descrição", "SAUDE");

        CategorizationResult result = strategy.categorize(context);

        assertThat(result.category()).isEqualTo("SAUDE");
        assertThat(result.riskLevel()).isNull();
    }

    @Test
    void categorize_deveRetornarNull_quandoTipoNaoInformado() {
        CategorizationContext context = new CategorizationContext(1L, "Título", "Descrição", null);

        CategorizationResult result = strategy.categorize(context);

        assertThat(result.category()).isNull();
    }
}
