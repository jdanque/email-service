package com.siteminder.emailservice.exception;

import com.siteminder.emailservice.model.Exchange;

/**
 *  Exception occurred during execution/processing of an {@link Exchange}.
 */
public class EmailServiceExecutionException extends RuntimeException{

    private final Exchange exchange;

    public EmailServiceExecutionException(String message, Exchange exchange){
        super(message);
        this.exchange = exchange;
    }

    public EmailServiceExecutionException(String message, Exchange exchange,  Throwable cause) {
        super(message, cause);
        this.exchange = exchange;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public static EmailServiceExecutionException wrapThrowable(Exchange exchange, Throwable e){
        //prevent double wrapping
        if(e instanceof EmailServiceExecutionException){
            return (EmailServiceExecutionException) e;
        } else {
            return new EmailServiceExecutionException("Exception occured during execution",exchange , e);
        }
    }
}
