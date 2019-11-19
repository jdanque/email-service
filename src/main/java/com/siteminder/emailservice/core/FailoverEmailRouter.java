package com.siteminder.emailservice.core;

import com.siteminder.emailservice.exception.EmailServiceException;
import com.siteminder.emailservice.model.Exchange;
import com.siteminder.emailservice.provider.EmailServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This FailoverEmailRouter would failover to use the next {@link EmailServiceProvider}
 * when an exception occured.
 */
public class FailoverEmailRouter implements FailoverSupport{

    private static final Logger LOGGER = LoggerFactory.getLogger(FailoverEmailRouter.class);

    private final List<Class<?>> exceptions;
    private final AtomicInteger lastGoodIndex = new AtomicInteger(-1);
    private final AtomicReference<EmailServiceProvider[]> emailServiceProviders = new AtomicReference<>(new EmailServiceProvider[0]);

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private FailoverEmailRouterProperties properties;

    public FailoverEmailRouter() {
        this.exceptions = new ArrayList<>();
    }

    public FailoverEmailRouter(List<Class<?>> exceptions) {
        this.exceptions = exceptions;

        // validate its all exception types
        for (Class<?> type : exceptions) {
            if (!type.isAssignableFrom(Throwable.class)) {
                throw new IllegalArgumentException("Class is not an instance of Throwable: " + type);
            }
        }

    }

    public void addProvider(EmailServiceProvider emailServiceProvider) {
        emailServiceProviders.updateAndGet(op -> doAdd(emailServiceProvider, op));
    }

    private EmailServiceProvider[] doAdd(EmailServiceProvider provider, EmailServiceProvider[] op){
        int len = op.length;
        EmailServiceProvider[] copy = Arrays.copyOf(op, len + 1, op.getClass());
        copy[len] = provider;
        return copy;
    }

    @Override
    public void process(Exchange exchange, FailoverCallback callback) {
        executorService.execute(new State(exchange, callback, emailServiceProviders.get())::run);
    }

    public boolean shouldFailover(Exchange exchange){
        if(exchange == null){
            return false;
        }

        if(exchange.isSkipped()){
            return true;
        }

        boolean result = false;
        if (exchange.getException() != null) {
            if (exceptions == null || exceptions.isEmpty()) {
                //  failover if no exceptions defined
                result = true;
            } else {
                // will look in exception hierarchy
                for (Class<?> exception : exceptions) {
                    if (exchange.getException(exception) != null) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    protected class State{
        final Exchange exchange;
        final EmailServiceProvider[] emailServiceProviders;
        final FailoverCallback callback;
        int index;
        int attempts;

        //use a copy for the exchange to avoid side effects to the original
        Exchange exchangeCopy;

        public State(Exchange exchange, FailoverCallback callback, EmailServiceProvider[] emailServiceProviders) {
            this.exchange = exchange;
            this.callback = callback;
            this.emailServiceProviders = emailServiceProviders;

            //retrieve the next provider
            if (properties.isSticky()) {
                int idx = lastGoodIndex.get();
                index = idx > 0 ? idx : 0;
            }

            LOGGER.debug("{} Failover starting with provider at index {}",exchange, index);
        }

        public void run()  {
            if(exchangeCopy != null && !shouldFailover(exchangeCopy)){
                lastGoodIndex.set(index);
                LOGGER.debug("{} Failover complete.", exchangeCopy);
                callback.done();
                return;
            }

            if(exchangeCopy != null){
                attempts++;
                int maxAttempts = properties.getMaxAttempts();

                if(maxAttempts > -1 && attempts > maxAttempts){
                    LOGGER.debug("{} Max attempts {} reached. Breaking out of failover.",exchangeCopy, maxAttempts);
                    callback.done();
                    return;
                }

                index++;
            }

            if(index >= emailServiceProviders.length){
                LOGGER.debug("{} Reached the end of providers available. Breaking out of failover.",exchangeCopy);
                callback.done();
                return;
            }

            //try again but copy the exchange before failover
            exchangeCopy =  exchange.copy();
            EmailServiceProvider provider = emailServiceProviders[index];

            //process the failover
            LOGGER.debug("{} Failover attempt {} ",exchangeCopy, attempts);
            provider.process(exchangeCopy, () -> executorService.execute(this::run));
        }

    }

    public AtomicInteger getLastGoodIndex() {
        return lastGoodIndex;
    }

    public AtomicReference<EmailServiceProvider[]> getEmailServiceProviders() {
        return emailServiceProviders;
    }

    public void addException(Class<?> exception){
        this.exceptions.add(exception);
    }
}
