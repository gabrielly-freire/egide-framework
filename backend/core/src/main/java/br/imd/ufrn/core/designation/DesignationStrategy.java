package br.imd.ufrn.core.designation;

/**
 * Estratégia para determinação automática do analista responsável por uma manifestação
 * (padrão Strategy).
 *
 * <p>A lógica de atribuição varia por instância:
 *
 * <ul>
 *   <li><b>Compliance:</b> baseada em hierarquia e cargos do analista candidato.</li>
 *   <li><b>Universidade:</b> analista do mesmo centro, excluídos os com conflito de interesse.</li>
 *   <li><b>Serviço Público:</b> designação automática por especialidade do órgão ou região afetada.</li>
 * </ul>
 *
 * <p>Retornar {@code null} indica que a designação não pode ser resolvida automaticamente e deve
 * ser feita manualmente via {@code POST /v1/assignments}. A implementação padrão
 * ({@link ManualDesignationStrategy}) sempre retorna {@code null}.
 */
public interface DesignationStrategy {

    /**
     * Determina o id do analista que deve ser designado responsável pela manifestação.
     *
     * @param context dados relevantes da manifestação
     * @return id do analista selecionado, ou {@code null} quando a designação é manual
     */
    Long resolve(DesignationContext context);
}
