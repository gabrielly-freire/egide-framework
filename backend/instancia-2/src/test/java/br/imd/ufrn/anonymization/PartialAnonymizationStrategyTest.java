package br.imd.ufrn.anonymization;

import static org.assertj.core.api.Assertions.assertThat;

import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationResult;
import org.junit.jupiter.api.Test;

class PartialAnonymizationStrategyTest {

    private final PartialAnonymizationStrategy strategy = new PartialAnonymizationStrategy();

    /** Aplica a anonimização passando o texto como descrição e devolve a descrição tratada. */
    private String anonymizeDescription(String text) {
        AnonymizationResult result = strategy.anonymize(
                new AnonymizationContext(true, "RECLAMAÇÃO", "Título", text));
        return result.description();
    }

    @Test
    void anonymize_deveMascararCpf() {
        String result = anonymizeDescription("Sou o aluno de CPF 123.456.789-00 e quero reclamar.");

        assertThat(result).isEqualTo("Sou o aluno de CPF [CPF] e quero reclamar.");
    }

    @Test
    void anonymize_deveMascararEmail() {
        String result = anonymizeDescription("Contato: joao.silva@alu.ufrn.br para retorno.");

        assertThat(result).isEqualTo("Contato: [E-MAIL] para retorno.");
    }

    @Test
    void anonymize_deveMascararTelefone() {
        String result = anonymizeDescription("Meu telefone é (84) 99999-8888 caso precisem.");

        assertThat(result).isEqualTo("Meu telefone é [TELEFONE] caso precisem.");
    }

    @Test
    void anonymize_deveMascararMatricula() {
        String result = anonymizeDescription("Aluno da matrícula 20210012345 do CCET.");

        assertThat(result).isEqualTo("Aluno da matrícula [MATRÍCULA] do CCET.");
    }

    @Test
    void anonymize_deveMascararMatricula_comPalavraDeLigacaoAntesDoNumero() {
        String result = anonymizeDescription("Minha matrícula é 20210012345 e o sistema caiu.");

        assertThat(result).isEqualTo("Minha matrícula é [MATRÍCULA] e o sistema caiu.");
    }

    @Test
    void anonymize_devePreservarPontoFinalAposEmail() {
        String result = anonymizeDescription("Meu e-mail é nicole@ufrn.br. O sistema caiu.");

        assertThat(result).isEqualTo("Meu e-mail é [E-MAIL]. O sistema caiu.");
    }

    @Test
    void anonymize_devePreservarNomeDoManifestante() {
        String text = "Meu nome é Nicole e o elevador está quebrado.";

        assertThat(anonymizeDescription(text)).isEqualTo(text);
    }

    @Test
    void anonymize_deveMascararFraseCompletaPreservandoNome() {
        String text = "Meu nome é Nicole, minha matrícula é 20210012345 "
                + "e meu e-mail é nicole@ufrn.br. O sistema caiu.";

        String result = anonymizeDescription(text);

        assertThat(result).isEqualTo("Meu nome é Nicole, minha matrícula é [MATRÍCULA] "
                + "e meu e-mail é [E-MAIL]. O sistema caiu.");
    }

    @Test
    void anonymize_deveMascararMultiplosIdentificadoresNoMesmoTexto() {
        String result = anonymizeDescription(
                "Matrícula 20210012345, CPF 123.456.789-00, fone (84) 99999-8888.");

        assertThat(result).isEqualTo("Matrícula [MATRÍCULA], CPF [CPF], fone [TELEFONE].");
    }

    @Test
    void anonymize_devePreservarTextoSemIdentificadores() {
        String text = "O elevador do bloco A está quebrado há duas semanas.";

        assertThat(anonymizeDescription(text)).isEqualTo(text);
    }

    @Test
    void anonymize_deveMascararTambemOTitulo() {
        AnonymizationResult result = strategy.anonymize(new AnonymizationContext(
                true, "RECLAMAÇÃO", "Contato joao@ufrn.br", "Texto sem identificadores."));

        assertThat(result.title()).isEqualTo("Contato [E-MAIL]");
        assertThat(result.description()).isEqualTo("Texto sem identificadores.");
    }

    @Test
    void anonymize_deveRetornarNull_quandoDescricaoForNull() {
        assertThat(anonymizeDescription(null)).isNull();
    }
}
