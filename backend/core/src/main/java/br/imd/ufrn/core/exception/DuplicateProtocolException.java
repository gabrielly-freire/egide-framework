package br.imd.ufrn.core.exception;

public class DuplicateProtocolException extends CoreException {

    public DuplicateProtocolException(String protocolNumber) {
        super("Número de protocolo já existe: " + protocolNumber);
    }
}
