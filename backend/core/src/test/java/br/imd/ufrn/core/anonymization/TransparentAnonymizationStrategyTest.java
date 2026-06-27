package br.imd.ufrn.core.anonymization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransparentAnonymizationStrategyTest {

    private final AnonymizationStrategy strategy = new TransparentAnonymizationStrategy();

    @Test
    void anonymize_deveRetornarOMesmoTexto_independenteDoContexto() {
        String text = "Fui assediado pelo analista João Silva no departamento de TI.";
        AnonymizationContext context = new AnonymizationContext(true, "ASSÉDIO");

        String result = strategy.anonymize(text, context);

        assertThat(result).isEqualTo(text);
    }

    @Test
    void anonymize_deveRetornarAMesmaInstancia() {
        String text = "Texto qualquer";
        AnonymizationContext context = new AnonymizationContext(false, "SUGESTÃO");

        String result = strategy.anonymize(text, context);

        assertThat(result).isSameAs(text);
    }

    @Test
    void anonymize_deveRetornarNull_quandoTextoForNull() {
        AnonymizationContext context = new AnonymizationContext(true, "DENÚNCIA");

        String result = strategy.anonymize(null, context);

        assertThat(result).isNull();
    }
}
