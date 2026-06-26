package br.imd.ufrn.core.exception;

public abstract class CoreException extends RuntimeException {

    protected CoreException(String message) {
        super(message);
    }

    protected CoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
