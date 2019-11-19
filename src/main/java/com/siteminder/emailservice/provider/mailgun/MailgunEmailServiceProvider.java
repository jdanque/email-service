package com.siteminder.emailservice.provider.mailgun;

import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.model.EmailUser;
import com.siteminder.emailservice.provider.BaseEmailServiceProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of an email service provider that caters to mailgun
 */
@Service
public class MailgunEmailServiceProvider extends BaseEmailServiceProvider<MultiValueMap<String, String>> {

    public static final String CONFIG_NAME = "mailgun";

    @Override
    protected String getConfigName() {
        return CONFIG_NAME;
    }

    @Override
    protected HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("api", properties.getApiKey());
        return headers;
    }

    @Override
    protected MultiValueMap<String, String> prepareBody(Email emailRequest) {
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();

        body.add("from", emailRequest.getSender().getEmail() );
        body.add("subject", emailRequest.getSubject());
        body.add("text", emailRequest.getContent());
        body.add("to",  joinEmailUserAddress(emailRequest.getRecipient()));

        if( emailRequest.getCc() != null && !emailRequest.getCc().isEmpty() ){
            body.add("cc", joinEmailUserAddress(emailRequest.getCc()));
        }

        if( emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty() ){
            body.add("bcc", joinEmailUserAddress(emailRequest.getBcc()));
        }

        return body;
    }

    private String joinEmailUserAddress(List<EmailUser> list){
        return list.stream()
                .map(EmailUser::getEmail)
                .collect(Collectors.joining(","));
    }

}
