package br.imd.ufrn.core.conflict;

/**
 * Estratégia para verificação de conflito de interesse (padrão Strategy).
 *
 * <p>Define se um analista está impedido de atuar em determinada manifestação.
 * As condições que constituem impedimento variam por instância:
 *
 * <ul>
 *   <li><b>Compliance:</b> analista hierarquicamente subordinado ao denunciado ou ao denunciante.</li>
 *   <li><b>Universidade:</b> analista pertencente ao mesmo centro/departamento do denunciado.</li>
 *   <li><b>Serviço Público:</b> impedimento legal por citação ou parentesco.</li>
 * </ul>
 *
 * <p>A implementação padrão ({@link NoConflictOfInterestStrategy}) não aplica nenhuma restrição
 * e é substituída pelo bean da instância via {@code @ConditionalOnMissingBean}.
 */
public interface ConflictOfInterestStrategy {

    /**
     * Verifica se o analista identificado pelo contexto tem conflito de interesse com a
     * manifestação.
     *
     * @param context dados da manifestação e do analista candidato
     * @return {@code true} se houver conflito e o analista <em>não puder</em> atuar
     */
    boolean hasConflict(ConflictOfInterestContext context);
}
