package br.imd.ufrn.core.categorization;

/**
 * Resultado da categorização de uma manifestação.
 *
 * <p>Ambos os campos são {@code String} e podem ser {@code null}, mantendo o Core genérico:
 * cada instância define seu próprio vocabulário de categorias (tipicamente um {@code enum})
 * e o converte para {@code String} na fronteira. {@code riskLevel} é {@code null} nas instâncias
 * que não trabalham com o conceito de risco.
 */
public record CategorizationResult(
        String category,
        String riskLevel
) {}
