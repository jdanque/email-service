package com.siteminder.emailservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.siteminder.utils.CommonTestUtils.createValidEmailRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(EmailController.class)
public class EmailControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmailService service;

    private ObjectMapper objectMapper;
    private Email request;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        request = createValidEmailRequest();
    }

    @Test
    public void whenEmailSend_givenEmptyBody_thenReturnBadRequest() throws Exception {
        mvc.perform(post("/email/send")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenEmailSend_givenBlankRequiredFields_thenReturnBadRequest() throws Exception {

        request.setSubject(null);
        mvc.perform(post("/email/send")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        request = createValidEmailRequest();
        request.setContent(null);
        mvc.perform(post("/email/send")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        request = createValidEmailRequest();
        request.setSender(null);
        mvc.perform(post("/email/send")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        request = createValidEmailRequest();
        request.setRecipient(null);
        mvc.perform(post("/email/send")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void whenEmailSend_givenValidRequest_shouldReturn201() throws Exception {
        mvc.perform(post("/email/send")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

}