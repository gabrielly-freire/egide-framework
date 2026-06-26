package br.imd.ufrn.core.exception;

public class EvaluationAlreadyExistsException extends CoreException {

    public EvaluationAlreadyExistsException(Long manifestationId) {
        super("Já existe uma avaliação para a manifestação: " + manifestationId);
    }
}
