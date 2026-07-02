package br.imd.ufrn.core.anonymization;

/**
 * Resultado da anonimização: título e descrição já tratados. Permite que a estratégia anonimize
 * ambos os campos numa única passagem (uma única chamada externa, quando houver).
 */
public record AnonymizationResult(
        String title,
        String description
) {}
