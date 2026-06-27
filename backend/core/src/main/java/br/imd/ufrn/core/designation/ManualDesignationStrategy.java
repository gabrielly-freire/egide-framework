package br.imd.ufrn.core.designation;

// Implementação padrão: designação sempre manual (retorna null).
// Substituída pelo bean da instância via @ConditionalOnMissingBean.
public class ManualDesignationStrategy implements DesignationStrategy {

    @Override
    public Long resolve(DesignationContext context) {
        return null;
    }
}
