package br.imd.ufrn.core.conflict;

// Implementação padrão: não aplica nenhuma restrição de conflito de interesse.
// Substituída pelo bean da instância via @ConditionalOnMissingBean.
public class NoConflictOfInterestStrategy implements ConflictOfInterestStrategy {

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        return false;
    }
}
