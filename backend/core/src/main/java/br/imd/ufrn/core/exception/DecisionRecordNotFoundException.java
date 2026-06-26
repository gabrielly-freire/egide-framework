package br.imd.ufrn.core.exception;

public class DecisionRecordNotFoundException extends CoreException {

    public DecisionRecordNotFoundException(Long id) {
        super("Decisão/parecer não encontrado com id: " + id);
    }
}
