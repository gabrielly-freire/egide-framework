package br.imd.ufrn.atendimento.exception;

import br.imd.ufrn.core.exception.CoreException;

public class LegalImpedimentNotFoundException extends CoreException {

    public LegalImpedimentNotFoundException(Long id) {
        super("Impedimento legal não encontrado com id: " + id);
    }
}
