package br.imd.ufrn.exception;

import br.imd.ufrn.core.exception.CoreException;

public class AccusationNotFoundException extends CoreException {

    public AccusationNotFoundException(Long manifestationId) {
        super("Nenhum denunciado registrado para a manifestação: " + manifestationId);
    }
}
