package com.siteminder.emailservice.provider.sendgrid;

import com.siteminder.emailservice.model.EmailUser;

import java.util.ArrayList;
import java.util.List;

/**
 * The main request object for a sendgrid transaction
 */
public class SendGridRequest {
    private List<Content> content;
    private EmailUser from;
    private String subject;
    private List<Personalization> personalization;

    public SendGridRequest() {
        content = new ArrayList<>();
        personalization = new ArrayList<>();
    }

    public List<Content> getContent() {
        return content;
    }

    public void addContent(Content content) {
        this.content.add(content);
    }

    public EmailUser getFrom() {
        return from;
    }

    public void setFrom(EmailUser from) {
        this.from = from;
    }

    public List<Personalization> getPersonalization() {
        return personalization;
    }

    public void addPersonalization(Personalization personalization) {
        this.personalization.add(personalization);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
