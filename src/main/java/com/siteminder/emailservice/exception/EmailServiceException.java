package com.siteminder.emailservice.exception;

/**
 * General exception for the service
 */
public class EmailServiceException extends RuntimeException {

    private static final long serialVersionUID = -2346973080773623662L;

    public EmailServiceException() {
        super();
    }

    public EmailServiceException(String message) {
        super(message);
    }
}
