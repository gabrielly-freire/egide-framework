package br.imd.ufrn.core.exception;

public class AutoAssignmentUnavailableException extends CoreException {

    public AutoAssignmentUnavailableException(Long manifestationId) {
        super("Designação automática não disponível para a manifestação id: " + manifestationId);
    }
}
