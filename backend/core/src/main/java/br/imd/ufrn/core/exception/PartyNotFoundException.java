package br.imd.ufrn.core.exception;

public class PartyNotFoundException extends CoreException {

    public PartyNotFoundException(Long id) {
        super("Parte não encontrada com id: " + id);
    }
}
