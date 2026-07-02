package br.imd.ufrn.core.anonymization;

/**
 * Dados de entrada para a anonimização de uma manifestação: os textos a anonimizar
 * ({@code title}/{@code description}) e o contexto que orienta a decisão
 * ({@code anonymous}/{@code type}).
 */
public record AnonymizationContext(
        boolean anonymous,
        String type,
        String title,
        String description
) {}
