package br.imd.ufrn.atendimento.web;

import br.imd.ufrn.atendimento.exception.AnalystNotFoundException;
import br.imd.ufrn.atendimento.exception.LegalImpedimentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InstanceExceptionHandler {

    @ExceptionHandler(AnalystNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleAnalystNotFound(AnalystNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Analista não encontrado");
        return detail;
    }

    @ExceptionHandler(LegalImpedimentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleLegalImpedimentNotFound(LegalImpedimentNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Impedimento legal não encontrado");
        return detail;
    }
}
