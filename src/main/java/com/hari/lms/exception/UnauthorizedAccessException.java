package com.hari.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user is not authorized to perform an action.
 * 
 * @author Hari Parthu
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}