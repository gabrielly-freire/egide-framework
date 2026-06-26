package br.imd.ufrn.core.exception;

public class AuditEntryNotFoundException extends CoreException {

    public AuditEntryNotFoundException(Long id) {
        super("Registro de auditoria não encontrado com id: " + id);
    }
}
