package br.imd.ufrn.anonymization;

import static org.assertj.core.api.Assertions.assertThat;

import br.imd.ufrn.core.anonymization.AnonymizationContext;
import org.junit.jupiter.api.Test;

class PartialAnonymizationStrategyTest {

    private final PartialAnonymizationStrategy strategy = new PartialAnonymizationStrategy();

    private static final AnonymizationContext CONTEXT =
            new AnonymizationContext(true, "RECLAMAÇÃO");

    @Test
    void anonymize_deveMascararCpf() {
        String text = "Sou o aluno de CPF 123.456.789-00 e quero reclamar.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Sou o aluno de CPF [CPF] e quero reclamar.");
    }

    @Test
    void anonymize_deveMascararEmail() {
        String text = "Contato: joao.silva@alu.ufrn.br para retorno.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Contato: [E-MAIL] para retorno.");
    }

    @Test
    void anonymize_deveMascararTelefone() {
        String text = "Meu telefone é (84) 99999-8888 caso precisem.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Meu telefone é [TELEFONE] caso precisem.");
    }

    @Test
    void anonymize_deveMascararMatricula() {
        String text = "Aluno da matrícula 20210012345 do CCET.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Aluno da matrícula [MATRÍCULA] do CCET.");
    }

    @Test
    void anonymize_deveMascararMatricula_comPalavraDeLigacaoAntesDoNumero() {
        String text = "Minha matrícula é 20210012345 e o sistema caiu.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Minha matrícula é [MATRÍCULA] e o sistema caiu.");
    }

    @Test
    void anonymize_devePreservarPontoFinalAposEmail() {
        String text = "Meu e-mail é nicole@ufrn.br. O sistema caiu.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Meu e-mail é [E-MAIL]. O sistema caiu.");
    }

    @Test
    void anonymize_devePreservarNomeDoManifestante() {
        String text = "Meu nome é Nicole e o elevador está quebrado.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo(text);
    }

    @Test
    void anonymize_deveMascararFraseCompletaPreservandoNome() {
        String text = "Meu nome é Nicole, minha matrícula é 20210012345 "
                + "e meu e-mail é nicole@ufrn.br. O sistema caiu.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo("Meu nome é Nicole, minha matrícula é [MATRÍCULA] "
                + "e meu e-mail é [E-MAIL]. O sistema caiu.");
    }

    @Test
    void anonymize_deveMascararMultiplosIdentificadoresNoMesmoTexto() {
        String text = "Matrícula 20210012345, CPF 123.456.789-00, fone (84) 99999-8888.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result)
                .isEqualTo("Matrícula [MATRÍCULA], CPF [CPF], fone [TELEFONE].");
    }

    @Test
    void anonymize_devePreservarTextoSemIdentificadores() {
        String text = "O elevador do bloco A está quebrado há duas semanas.";

        String result = strategy.anonymize(text, CONTEXT);

        assertThat(result).isEqualTo(text);
    }

    @Test
    void anonymize_deveRetornarNull_quandoTextoForNull() {
        String result = strategy.anonymize(null, CONTEXT);

        assertThat(result).isNull();
    }
}
