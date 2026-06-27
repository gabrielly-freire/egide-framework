package br.imd.ufrn.core.exception;

public class WorkflowAdvanceNotAllowedException extends CoreException {

    public WorkflowAdvanceNotAllowedException(Long manifestationId) {
        super("Não é possível avançar o workflow da manifestação id: " + manifestationId);
    }
}
