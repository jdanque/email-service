package com.siteminder.emailservice.model;

/**
 * An exchange is a message container holding the information of a message
 * @see {@link Message}
 */
public interface Exchange {

    String getExchangeId();
    void setExchangeId(String exchangeId);

    Exchange copy();

    Message getInput();
    void setInput(Message input);

    Message getOutput();
    void setOutput(Message output);

    boolean hasOutput();

    Message getMessage();
    void setMessage(Message message);

    Exception getException();

    <T> T getException(Class<T> type);
    void setException(Throwable t);

    boolean isSkipped();
    void setSkipped(boolean skipped);
}
