package br.imd.ufrn.core.exception;

public class ServiceEvaluationNotFoundException extends CoreException {

    public ServiceEvaluationNotFoundException(Long id) {
        super("Avaliação não encontrada com id: " + id);
    }

    public ServiceEvaluationNotFoundException(Long manifestationId, boolean byManifestation) {
        super("Avaliação não encontrada para a manifestação: " + manifestationId);
    }
}
