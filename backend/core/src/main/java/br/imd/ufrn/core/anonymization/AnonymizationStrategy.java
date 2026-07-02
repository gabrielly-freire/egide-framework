package br.imd.ufrn.core.anonymization;

/**
 * Define o contrato para anonimização de uma manifestação (título e descrição).
 * Cada instância fornece sua própria implementação como Spring bean.
 * O Core registra {@link TransparentAnonymizationStrategy} como padrão via auto-configuração.
 */
public interface AnonymizationStrategy {

    AnonymizationResult anonymize(AnonymizationContext context);
}
