package br.imd.ufrn.exception;

import br.imd.ufrn.core.exception.CoreException;

public class AcademicMemberNotFoundException extends CoreException {

    public AcademicMemberNotFoundException(Long id) {
        super("Membro acadêmico não encontrado com id: " + id);
    }
}
