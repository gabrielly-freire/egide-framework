package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;

public interface DesignationService {

    /**
     * Determina e atribui automaticamente um responsável à manifestação, aplicando as regras
     * de designação e de conflito de interesse configuradas pela instância.
     *
     * @param manifestationId id da manifestação a ser designada
     * @return designação criada
     * @throws br.imd.ufrn.core.exception.AutoAssignmentUnavailableException se a estratégia não
     *         puder determinar um responsável automaticamente
     * @throws br.imd.ufrn.core.exception.ConflictOfInterestException se o analista selecionado
     *         tiver conflito de interesse
     */
    ResponsibleAssignmentResponse autoAssign(Long manifestationId);

    /**
     * Verifica se o analista tem conflito de interesse com a manifestação.
     *
     * @param manifestationId id da manifestação
     * @param analystId       id do analista a verificar
     * @return {@code true} se houver conflito
     */
    boolean hasConflict(Long manifestationId, Long analystId);
}
