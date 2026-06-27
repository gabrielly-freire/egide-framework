package br.imd.ufrn.core.exception;

public class WorkflowAppealNotAllowedException extends CoreException {

    public WorkflowAppealNotAllowedException(Long manifestationId) {
        super("Recurso não permitido para a manifestação id: " + manifestationId);
    }
}
