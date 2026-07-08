package br.imd.ufrn.core.categorization;

/**
 * Dados de entrada para a triagem/categorização de uma manifestação.
 *
 * <p>Contém apenas o texto da manifestação; a forma de classificar (modelo, microserviço,
 * palavras-chave) é responsabilidade da {@link CategorizationStrategy} de cada instância.
 */
public record CategorizationContext(
        Long manifestationId,
        String title,
        String description,
        String type
) {}
