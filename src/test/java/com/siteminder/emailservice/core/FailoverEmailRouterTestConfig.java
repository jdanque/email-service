package com.siteminder.emailservice.core;

import com.siteminder.emailservice.provider.BaseEmailServiceProvider;
import com.siteminder.emailservice.provider.EmailServiceProvider;
import com.siteminder.emailservice.provider.config.EmailServiceProviderConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@TestConfiguration
class FailoverEmailRouterTestConfig {

    @Bean
    public EmailServiceProvider mockEmailServiceProvider1(){
        return mock(BaseEmailServiceProvider.class);
    }

    @Bean
    public EmailServiceProvider mockEmailServiceProvider2(){
        return mock(BaseEmailServiceProvider.class);
    }

    @Bean
    public EmailServiceProvider mockFailingServiceProvider(){
        return spy(TestFailingServiceProvider.class);
    }

    @Bean
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public EmailServiceProviderConfig mockEmailServiceProviderConfig(){
        return mock(EmailServiceProviderConfig.class);
    }

    @Bean(name = "failoverEmailRouter")
    public FailoverEmailRouter failoverEmailRouter(){
        FailoverEmailRouter failoverEmailRouter = new FailoverEmailRouter();
        failoverEmailRouter.addProvider(mockEmailServiceProvider1());
        failoverEmailRouter.addProvider(mockEmailServiceProvider2());
        return failoverEmailRouter;
    }

    @Bean(name = "failoverRouterWithFailingMockService")
    public FailoverEmailRouter failoverRouterWithFailingMockService(){
        FailoverEmailRouter failoverEmailRouter = new FailoverEmailRouter();
        failoverEmailRouter.addProvider(mockFailingServiceProvider());
        failoverEmailRouter.addProvider(mockEmailServiceProvider2());
        return failoverEmailRouter;
    }
}
