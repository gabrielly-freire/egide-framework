package br.imd.ufrn.core.anonymization;

/**
 * Define o contrato para anonimização do texto de uma manifestação.
 * Cada instância fornece sua própria implementação como Spring bean.
 * O Core registra {@link TransparentAnonymizationStrategy} como padrão via auto-configuração.
 */
public interface AnonymizationStrategy {

    String anonymize(String text, AnonymizationContext context);
}
