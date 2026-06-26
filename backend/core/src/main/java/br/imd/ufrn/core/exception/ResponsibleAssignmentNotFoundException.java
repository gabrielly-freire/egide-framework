package br.imd.ufrn.core.exception;

public class ResponsibleAssignmentNotFoundException extends CoreException {

    public ResponsibleAssignmentNotFoundException(Long id) {
        super("Designação não encontrada com id: " + id);
    }

    public ResponsibleAssignmentNotFoundException(Long manifestationId, boolean byManifestation) {
        super("Designação não encontrada para a manifestação: " + manifestationId);
    }
}
