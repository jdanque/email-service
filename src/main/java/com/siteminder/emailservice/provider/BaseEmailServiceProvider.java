package com.siteminder.emailservice.provider;

import com.siteminder.emailservice.core.FailoverCallback;
import com.siteminder.emailservice.exception.EmailServiceExecutionException;
import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.model.Exchange;
import com.siteminder.emailservice.provider.config.EmailServiceProviderConfig;
import com.siteminder.emailservice.provider.config.EmailServiceProviderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Baseclass for an email service provider.
 * Sets up the header and body of a request to a service provider and processes
 * it.
 * @param <T> the body type of a request to a service provider. this enables
 *           different body types support such as multimap or json object
 */
public abstract class BaseEmailServiceProvider<T> implements EmailServiceProvider{

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected EmailServiceProviderProperties properties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailServiceProviderConfig providerConfig;

    protected abstract String getConfigName();
    protected abstract HttpHeaders prepareHeaders();
    protected abstract T prepareBody(Email emailRequest);

    @Override
    public void process(Exchange exchange, FailoverCallback callback) {
        properties = providerConfig.getProvidersConfig().get(getConfigName());

        exchange.setSkipped(false);
        if( !properties.isEnabled() ){
            LOGGER.info("Provider {} is disabled. Skipping.", getConfigName());
            exchange.setSkipped(true);
            callback.done();
            return;
        }

        LOGGER.info("{} Sending via {}",exchange, getConfigName());
        HttpHeaders headers = prepareHeaders();
        T body = prepareBody((Email) exchange.getInput());

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(properties.getUrl(),
                            HttpMethod.POST,
                            new HttpEntity<>(body, headers),
                            String.class);

            if(response.getStatusCode().isError()) {
                exchange.setException(
                        new EmailServiceExecutionException(
                                response.getStatusCode().getReasonPhrase() + " " + response.getBody(),
                                exchange));
            }else{
                LOGGER.info("{} Sent via {}",exchange, getConfigName());
            }

        }catch (RestClientException e){
            LOGGER.error("{} Failed to send via {}",exchange, getConfigName(), e);
            exchange.setException(e);
        }finally {
            callback.done();
        }

    }
}
