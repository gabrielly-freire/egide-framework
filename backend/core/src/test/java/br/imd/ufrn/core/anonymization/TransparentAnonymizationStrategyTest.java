package br.imd.ufrn.core.anonymization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransparentAnonymizationStrategyTest {

    private final AnonymizationStrategy strategy = new TransparentAnonymizationStrategy();

    @Test
    void anonymize_deveRetornarTituloEDescricaoInalterados() {
        AnonymizationContext context = new AnonymizationContext(
                true, "ASSÉDIO", "Título original",
                "Fui assediado pelo analista João Silva no departamento de TI.");

        AnonymizationResult result = strategy.anonymize(context);

        assertThat(result.title()).isEqualTo("Título original");
        assertThat(result.description())
                .isEqualTo("Fui assediado pelo analista João Silva no departamento de TI.");
    }

    @Test
    void anonymize_devePropagarNulls() {
        AnonymizationContext context = new AnonymizationContext(false, "SUGESTÃO", null, null);

        AnonymizationResult result = strategy.anonymize(context);

        assertThat(result.title()).isNull();
        assertThat(result.description()).isNull();
    }
}
