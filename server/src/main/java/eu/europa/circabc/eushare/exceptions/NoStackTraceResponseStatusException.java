package eu.europa.circabc.eushare.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoStackTraceResponseStatusException extends ResponseStatusException {

    public NoStackTraceResponseStatusException(HttpStatus status) {
        super(status);
    }

    public NoStackTraceResponseStatusException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public NoStackTraceResponseStatusException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; 
    }
}
