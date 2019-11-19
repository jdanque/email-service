package com.siteminder.emailservice.core;

import com.siteminder.emailservice.model.EmailExchange;
import com.siteminder.emailservice.model.Exchange;
import com.siteminder.emailservice.provider.EmailServiceProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(FailoverEmailRouterTestConfig.class)
@RunWith(SpringRunner.class)
public class FailoverEmailRouterTest {

    @Autowired
    @Qualifier("failoverEmailRouter")
    private FailoverEmailRouter failoverEmailRouter;

    @Autowired
    @Qualifier("failoverRouterWithFailingMockService")
    private FailoverEmailRouter failoverRouterWithFailingMockService;

    @MockBean
    private FailoverEmailRouterProperties properties;

    @Autowired
    private EmailServiceProvider mockEmailServiceProvider1;

    @Autowired
    private EmailServiceProvider mockEmailServiceProvider2;

    @Autowired
    private EmailServiceProvider mockFailingServiceProvider;

    @Before
    public void setUp() {
        when(properties.getMaxAttempts()).thenReturn(-1);
    }

    @Test
    public void whenAddProvider_givenValidInput_thenUpdateProviderList() {
        //not using the one from test config
        FailoverEmailRouter failoverEmailRouter = spy(new FailoverEmailRouter());

        assertThat(failoverEmailRouter.getEmailServiceProviders().get().length, is(0));
        failoverEmailRouter.addProvider(mock(EmailServiceProvider.class));

        assertThat(failoverEmailRouter.getEmailServiceProviders().get().length, is(1));
    }


    @Test
    public void whenShouldFailover_givenExchange_thenReturnResult() {
        assertFalse(failoverEmailRouter.shouldFailover(null));

        Exchange exchange = mock(Exchange.class);
        assertFalse(failoverEmailRouter.shouldFailover(exchange));
        when(exchange.isSkipped()).thenReturn(true);
        assertTrue(failoverEmailRouter.shouldFailover(exchange));

        //no defined exceptions
        when(exchange.isSkipped()).thenReturn(false);
        when(exchange.getException()).thenReturn(new IOException());
        assertTrue(failoverEmailRouter.shouldFailover(exchange));

        when(exchange.getException()).thenReturn(new IOException());
        when(exchange.getException(IOException.class)).thenReturn(new IOException());
        failoverEmailRouter.addException(NumberFormatException.class);
        assertFalse(failoverEmailRouter.shouldFailover(exchange));
        failoverEmailRouter.addException(IOException.class);
        assertTrue(failoverEmailRouter.shouldFailover(exchange));
    }

    @Test
    public void whenProcess_givenNoExceptions_thenFailoverToFirstOnly() {
        Exchange exchange = new EmailExchange();

        failoverEmailRouter.process(exchange, () -> {});
        verify(mockEmailServiceProvider1,times(1))
                .process(any(Exchange.class), any());
        verify(mockEmailServiceProvider2,never())
                .process(any(Exchange.class), any());
    }

    @Test
    public void whenProcess_givenExchangeFailedOnFirst_thenFailoverToSecond() {
        Exchange exchange = new EmailExchange();
        exchange.setException(new IOException());

        failoverRouterWithFailingMockService.process(exchange, () -> {});
        verify(mockFailingServiceProvider,times(1))
                .process(any(Exchange.class), any());
        verify(mockEmailServiceProvider2,times(1))
                .process(any(Exchange.class), any());
    }

}