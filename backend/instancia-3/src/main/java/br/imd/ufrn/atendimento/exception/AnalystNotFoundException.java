package br.imd.ufrn.atendimento.exception;

import br.imd.ufrn.core.exception.CoreException;

public class AnalystNotFoundException extends CoreException {

    public AnalystNotFoundException(Long id) {
        super("Analista não encontrado com id: " + id);
    }
}
