package com.siteminder.emailservice.service;

import com.siteminder.emailservice.core.FailoverEmailRouter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.siteminder.utils.CommonTestUtils.createValidEmailRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @Mock
    private FailoverEmailRouter failoverEmailRouter;
    
    @InjectMocks
    private EmailService service;

    @Test
    public void whenSend_givenEmailRequest_shouldCallFailover() {
        service.send(createValidEmailRequest());
        verify(failoverEmailRouter, times(1)).process(any(),any());
    }
}