package com.java.erp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a deactivated user attempts to authenticate.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountDeactivatedException extends RuntimeException {

    public AccountDeactivatedException(String message) {
        super(message);
    }
}
