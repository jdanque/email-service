package com.siteminder.emailservice.model;

import com.siteminder.emailservice.exception.EmailServiceExecutionException;

import java.util.UUID;

/**
 * An implementation of an {@link Exchange} for an email
 */
public class EmailExchange implements Exchange{

    private String exchangeId;
    private Message input;
    private Message output;
    private Exception exception;
    private boolean skipped;

    public EmailExchange() {
        this.exchangeId = UUID.randomUUID().toString();
    }

    @Override
    public String getExchangeId() {
        return exchangeId == null ? UUID.randomUUID().toString() : exchangeId;
    }

    @Override
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Override
    public Exchange copy() {
        EmailExchange exchange = new EmailExchange();

        exchange.setExchangeId(getExchangeId());
        exchange.setInput(getInput().copy());
        if(hasOutput()){
            exchange.setOutput(getOutput().copy());
        }
        exchange.setException(getException());

        return exchange;
    }

    @Override
    public boolean hasOutput() {
        return output != null;
    }

    @Override
    public Message getInput() {
        if (input == null) {
            input = new Email();
        }

        return input;
    }

    @Override
    public void setInput(Message input) {
        this.input = input;
    }

    @Override
    public Message getOutput() {
        return this.output;
    }

    @Override
    public void setOutput(Message output) {
        this.output = output;
    }

    @Override
    public Message getMessage() {
        return hasOutput() ? getOutput() : getInput();
    }

    @Override
    public void setMessage(Message message) {
        if(hasOutput()){
            setOutput(message);
        }else{
            setInput(message);
        }
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public <T> T getException(Class<T> type) {
        return null;
    }

    @Override
    public void setException(Throwable t) {
        if(t == null){
            this.exception = null;
        } else if (t instanceof Exception) {
            this.exception = (Exception) t;
        }else {
            this.exception = EmailServiceExecutionException.wrapThrowable(this, t);
        }
    }

    @Override
    public boolean isSkipped() {
        return skipped;
    }

    @Override
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public String toString() {
        return String.format("EmailExchange[%s]", exchangeId == null ? "" : exchangeId);
    }
}
