package br.imd.ufrn.categorization;

import static org.assertj.core.api.Assertions.assertThat;

import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import org.junit.jupiter.api.Test;

class UniversityCategorizationStrategyTest {

    private final UniversityCategorizationStrategy strategy = new UniversityCategorizationStrategy();

    private CategorizationResult categorize(String title, String description) {
        return strategy.categorize(new CategorizationContext(1L, title, description, "RECLAMACAO"));
    }

    @Test
    void categorize_deveClassificarComoInfraestrutura() {
        CategorizationResult result =
                categorize("Elevador quebrado", "O elevador do bloco A está parado há dias.");

        assertThat(result.category()).isEqualTo("INFRAESTRUTURA");
    }

    @Test
    void categorize_deveClassificarComoMatricula() {
        CategorizationResult result =
                categorize("Problema na matrícula", "Não consigo fazer minha rematrícula no SIGAA.");

        assertThat(result.category()).isEqualTo("MATRICULA");
    }

    @Test
    void categorize_deveClassificarComoAtendimento() {
        CategorizationResult result =
                categorize("Demora", "Fui mal atendido na secretaria, muita demora na fila.");

        assertThat(result.category()).isEqualTo("ATENDIMENTO");
    }

    @Test
    void categorize_deveClassificarComoAcademico() {
        CategorizationResult result =
                categorize("Nota errada", "A nota da prova da disciplina do professor está incorreta.");

        assertThat(result.category()).isEqualTo("ACADEMICO");
    }

    @Test
    void categorize_deveClassificarComoFinanceiro() {
        CategorizationResult result =
                categorize("Bolsa", "Não recebi minha bolsa de auxílio permanência.");

        assertThat(result.category()).isEqualTo("FINANCEIRO");
    }

    @Test
    void categorize_deveCairEmOutros_quandoNaoReconhece() {
        CategorizationResult result =
                categorize("Sugestão", "Gostaria de parabenizar a equipe pelo trabalho.");

        assertThat(result.category()).isEqualTo("OUTROS");
    }

    @Test
    void categorize_deveIgnorarAcentoECaixa() {
        CategorizationResult result =
                categorize("MATRÍCULA", "Trancamento de INSCRIÇÃO pendente.");

        assertThat(result.category()).isEqualTo("MATRICULA");
    }

    @Test
    void categorize_deveEscolherCategoriaComMaisOcorrencias() {
        // 1 palavra de ATENDIMENTO ("secretaria") vs 2 de INFRAESTRUTURA ("elevador", "manutenção").
        CategorizationResult result =
                categorize("Elevador", "Pedi na secretaria a manutenção do elevador.");

        assertThat(result.category()).isEqualTo("INFRAESTRUTURA");
    }

    @Test
    void categorize_deveRetornarRiskLevelNulo() {
        CategorizationResult result =
                categorize("Elevador quebrado", "O elevador está parado.");

        assertThat(result.riskLevel()).isNull();
    }

    @Test
    void categorize_naoDeveCasarPalavraChavePorSubstring() {
        // "ru" (Restaurante Universitário) não pode casar dentro de "estrutura".
        CategorizationResult result =
                categorize("Estrutura", "A estrutura do prédio precisa de reparo.");

        assertThat(result.category()).isEqualTo("INFRAESTRUTURA");
    }
}
