package br.imd.ufrn.web;

import br.imd.ufrn.exception.AcademicMemberNotFoundException;
import br.imd.ufrn.exception.AccusationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratamento das exceções próprias da Instância 2, seguindo o mesmo formato {@code ProblemDetail}
 * (RFC 9457) do {@code GlobalExceptionHandler} do Core. Convive com o handler do Core; o Spring
 * seleciona o handler mais específico para cada exceção.
 */
@RestControllerAdvice
public class InstanceExceptionHandler {

    @ExceptionHandler(AcademicMemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleAcademicMemberNotFound(AcademicMemberNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Membro acadêmico não encontrado");
        return detail;
    }

    @ExceptionHandler(AccusationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleAccusationNotFound(AccusationNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Denunciado não encontrado");
        return detail;
    }
}
