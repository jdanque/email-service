package com.siteminder.emailservice.provider.sendgrid;

import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.provider.BaseEmailServiceProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 *  An implementation of an email service provider that caters to sendgrid
 */
@Service
public class SendGridEmailServiceProvider extends BaseEmailServiceProvider<SendGridRequest> {

    private static final String CONFIG_NAME = "sendgrid";

    @Override
    protected String getConfigName() {
        return CONFIG_NAME;
    }

    @Override
    protected HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer " + properties.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    protected SendGridRequest prepareBody(Email emailRequest) {

        SendGridRequest sendGridRequest = new SendGridRequest();
        sendGridRequest.setFrom(emailRequest.getSender());
        sendGridRequest.setSubject(emailRequest.getSubject());
        sendGridRequest.addContent(new Content(MediaType.TEXT_PLAIN_VALUE, emailRequest.getContent()));

        Personalization personalization = new Personalization();
        personalization.setTo(emailRequest.getRecipient());

        if( emailRequest.getCc() != null && !emailRequest.getCc().isEmpty() ) {
            personalization.setCc(emailRequest.getCc());
        }

        if( emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty() ) {
            personalization.setBcc(emailRequest.getBcc());
        }

        sendGridRequest.addPersonalization(personalization);

        return sendGridRequest;
    }

}
