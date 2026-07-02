package br.imd.ufrn.core.categorization;

// Implementação padrão (não classifica). Substituída por bean da instância via @ConditionalOnMissingBean.
public class NoOpCategorizationStrategy implements CategorizationStrategy {

    @Override
    public CategorizationResult categorize(CategorizationContext context) {
        return new CategorizationResult(null, null);
    }
}
