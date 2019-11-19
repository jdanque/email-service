package com.siteminder.emailservice.config;

import com.siteminder.emailservice.core.FailoverEmailRouter;
import com.siteminder.emailservice.provider.EmailServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main configuration for the whole service.
 * Would autowire all providers that implements {@link EmailServiceProvider}.
 */
@Configuration
public class EmailServiceConfig {

    @Autowired
    private List<EmailServiceProvider> providers;

    @Bean
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public FailoverEmailRouter failoverRouter(){
        FailoverEmailRouter failoverEmailRouter = new FailoverEmailRouter();
        providers.forEach(failoverEmailRouter::addProvider);
        return failoverEmailRouter;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
        restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(5));
        restTemplateBuilder.setReadTimeout(Duration.ofSeconds(5));
        return restTemplateBuilder.build();
    }


}
