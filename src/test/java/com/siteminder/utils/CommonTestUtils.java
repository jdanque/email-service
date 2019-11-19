package com.siteminder.utils;

import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.model.EmailUser;

import java.util.ArrayList;
import java.util.List;

public class CommonTestUtils {

    public static Email createValidEmailRequest(){
        Email request = new Email();
        request.setSubject("Subject");
        request.setContent("Content");

        EmailUser sender = new EmailUser();
        sender.setName("sender-name");
        sender.setEmail("sender@email.com");
        request.setSender(sender);

        List<EmailUser> recipients = new ArrayList<>();
        EmailUser recipient1 = new EmailUser();
        recipient1.setName("recipient1-name");
        recipient1.setEmail("recipient1@email.com");

        EmailUser recipient2 = new EmailUser();
        recipient2.setName("recipient1-name");
        recipient2.setEmail("recipient1@email.com");

        recipients.add(recipient1);
        recipients.add(recipient2);

        request.setRecipient(recipients);


        return request;
    }
}
