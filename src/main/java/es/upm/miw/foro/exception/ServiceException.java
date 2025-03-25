package es.upm.miw.foro.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    private final HttpStatus status;

    public ServiceException(String message) {
        super(message);
        this.status = null;
    }

    public ServiceException(Throwable cause) {
        super(cause);
        this.status = null;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.status = null;
    }

    public ServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
