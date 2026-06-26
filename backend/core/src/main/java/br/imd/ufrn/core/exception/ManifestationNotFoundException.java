package br.imd.ufrn.core.exception;

public class ManifestationNotFoundException extends CoreException {

    public ManifestationNotFoundException(Long id) {
        super("Manifestação não encontrada com id: " + id);
    }

    public ManifestationNotFoundException(String protocolNumber) {
        super("Manifestação não encontrada com protocolo: " + protocolNumber);
    }
}
