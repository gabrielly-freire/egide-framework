package br.imd.ufrn.core.web;

import br.imd.ufrn.core.exception.AuditEntryNotFoundException;
import br.imd.ufrn.core.exception.AutoAssignmentUnavailableException;
import br.imd.ufrn.core.exception.ConflictOfInterestException;
import br.imd.ufrn.core.exception.WorkflowAdvanceNotAllowedException;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import br.imd.ufrn.core.exception.DecisionRecordNotFoundException;
import br.imd.ufrn.core.exception.DuplicateProtocolException;
import br.imd.ufrn.core.exception.EvaluationAlreadyExistsException;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.exception.ResponsibleAssignmentNotFoundException;
import br.imd.ufrn.core.exception.ServiceEvaluationNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ManifestationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleManifestationNotFound(ManifestationNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Manifestação não encontrada");
        return detail;
    }

    @ExceptionHandler(DuplicateProtocolException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDuplicateProtocol(DuplicateProtocolException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setTitle("Protocolo duplicado");
        return detail;
    }

    @ExceptionHandler(ServiceEvaluationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleServiceEvaluationNotFound(ServiceEvaluationNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Avaliação não encontrada");
        return detail;
    }

    @ExceptionHandler(EvaluationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleEvaluationAlreadyExists(EvaluationAlreadyExistsException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setTitle("Avaliação já registrada");
        return detail;
    }

    @ExceptionHandler(AuditEntryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleAuditEntryNotFound(AuditEntryNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Registro de auditoria não encontrado");
        return detail;
    }

    @ExceptionHandler(DecisionRecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleDecisionRecordNotFound(DecisionRecordNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Decisão/parecer não encontrado");
        return detail;
    }

    @ExceptionHandler(ResponsibleAssignmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResponsibleAssignmentNotFound(ResponsibleAssignmentNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Designação não encontrada");
        return detail;
    }

    @ExceptionHandler(ConflictOfInterestException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleConflictOfInterest(ConflictOfInterestException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setTitle("Conflito de interesse");
        return detail;
    }

    @ExceptionHandler(AutoAssignmentUnavailableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleAutoAssignmentUnavailable(AutoAssignmentUnavailableException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setTitle("Designação automática indisponível");
        return detail;
    }

    @ExceptionHandler(WorkflowAdvanceNotAllowedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleWorkflowAdvanceNotAllowed(WorkflowAdvanceNotAllowedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setTitle("Avanço de workflow não permitido");
        return detail;
    }

    @ExceptionHandler(WorkflowAppealNotAllowedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleWorkflowAppealNotAllowed(WorkflowAppealNotAllowedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setTitle("Recurso não permitido");
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Erro de validação");
        detail.setTitle("Dados inválidos");
        detail.setProperty("errors", errors);
        return detail;
    }
}
