package br.imd.ufrn.categorization;

import java.util.Set;

/**
 * Vocabulário de categorias institucionais da Ouvidoria Universitária (Instância 2).
 *
 * <p>Cada categoria carrega as palavras-chave (já em minúsculas e sem acento) que a
 * {@link UniversityCategorizationStrategy} procura no texto da manifestação. {@link #OUTROS} é o
 * fallback, sem palavras-chave: vale quando nenhuma outra categoria é reconhecida.
 *
 * <p>Palavras-chave de uma só palavra casam por token inteiro, tolerando plural (evita
 * falso-positivo por substring); expressões com espaço casam por ocorrência no texto.
 */
public enum UniversityCategory {

    INFRAESTRUTURA(Set.of(
            "elevador", "sala", "banheiro", "laboratorio", "predio", "obra", "limpeza",
            "wifi", "internet", "rede", "agua", "energia", "luz", "climatizacao",
            "manutencao", "estrutura", "infraestrutura", "ar condicionado")),

    MATRICULA(Set.of(
            "matricula", "rematricula", "trancamento", "inscricao", "vaga",
            "sigaa", "matriculado", "matricular")),

    ATENDIMENTO(Set.of(
            "atendimento", "secretaria", "demora", "funcionario", "servidor",
            "guiche", "protocolo", "fila", "recepcao", "atendente")),

    ACADEMICO(Set.of(
            "nota", "prova", "disciplina", "professor", "aula", "frequencia",
            "tcc", "avaliacao", "ementa", "estagio", "monitoria", "plano de ensino")),

    FINANCEIRO(Set.of(
            "bolsa", "auxilio", "pagamento", "taxa", "reembolso", "mensalidade",
            "financeiro", "ru", "restaurante universitario")),

    OUTROS(Set.of());

    private final Set<String> keywords;

    UniversityCategory(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<String> keywords() {
        return keywords;
    }
}
