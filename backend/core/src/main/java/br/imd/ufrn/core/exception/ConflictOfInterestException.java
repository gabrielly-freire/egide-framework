package br.imd.ufrn.core.exception;

public class ConflictOfInterestException extends CoreException {

    public ConflictOfInterestException(Long analystId, Long manifestationId) {
        super("Analista id " + analystId + " possui conflito de interesse com a manifestação id " + manifestationId);
    }
}
