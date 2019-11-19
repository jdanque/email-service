package com.siteminder.emailservice.service;

import com.siteminder.emailservice.core.FailoverEmailRouter;
import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.model.EmailExchange;
import com.siteminder.emailservice.model.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Main service class for the email service which the controller has access to.
 * Uses a fail over router for sending an {@link Exchange} with an {@link Email}
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private FailoverEmailRouter failoverEmailRouter;

    public void send(Email request) {
        Exchange exchange = new EmailExchange();
        exchange.setInput(request);
        failoverEmailRouter.process(exchange,()->{});

    }
}
