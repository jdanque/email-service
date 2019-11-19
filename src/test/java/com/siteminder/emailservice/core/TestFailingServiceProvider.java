package com.siteminder.emailservice.core;

import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.model.Exchange;
import com.siteminder.emailservice.provider.BaseEmailServiceProvider;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

class TestFailingServiceProvider extends BaseEmailServiceProvider<String> {

    @Override
    protected String getConfigName() {
        return "";
    }

    @Override
    protected HttpHeaders prepareHeaders() {
        return null;
    }

    @Override
    protected String prepareBody(Email emailRequest) {
        return null;
    }

    @Override
    public void process(Exchange exchange, FailoverCallback callback) {
        exchange.setException(new IOException());
        callback.done();
    }
}
