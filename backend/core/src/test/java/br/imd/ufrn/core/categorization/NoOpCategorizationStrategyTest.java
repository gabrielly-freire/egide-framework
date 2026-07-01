package br.imd.ufrn.core.categorization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoOpCategorizationStrategyTest {

    private final CategorizationStrategy strategy = new NoOpCategorizationStrategy();

    @Test
    void categorize_deveRetornarResultadoVazio() {
        CategorizationResult result = strategy.categorize(
                new CategorizationContext(1L, "Título", "Descrição", "ASSÉDIO"));

        assertThat(result.category()).isNull();
        assertThat(result.riskLevel()).isNull();
    }
}
