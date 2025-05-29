package es.upm.miw.foro.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
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
}
