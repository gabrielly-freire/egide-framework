package br.imd.ufrn.core.anonymization;

// Implementação padrão (sem anonimização). Substituída por bean da instância via @ConditionalOnMissingBean.
public class TransparentAnonymizationStrategy implements AnonymizationStrategy {

    @Override
    public String anonymize(String text, AnonymizationContext context) {
        return text;
    }
}
