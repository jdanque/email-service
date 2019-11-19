package com.siteminder.emailservice.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A basic email message
 */
public class Email implements Message{

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    @Valid
    @NotNull
    private EmailUser sender;

    @NotNull(message = "Must have at least 1 recipient")
    private List<@Valid EmailUser> recipient;

    private List<@Valid EmailUser> cc;

    private List<@Valid EmailUser> bcc;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EmailUser getSender() {
        return sender;
    }

    public void setSender(EmailUser sender) {
        this.sender = sender;
    }

    public List<EmailUser> getRecipient() {
        return recipient;
    }

    public void setRecipient(List<EmailUser> recipient) {
        this.recipient = recipient;
    }

    public List<EmailUser> getCc() {
        return cc;
    }

    public void setCc(List<EmailUser> cc) {
        this.cc = cc;
    }

    public List<EmailUser> getBcc() {
        return bcc;
    }

    public void setBcc(List<EmailUser> bcc) {
        this.bcc = bcc;
    }

    @Override
    public Email copy() {
        Email copy = new Email();
        copy.setSubject(this.getSubject());
        copy.setContent(this.getContent());
        copy.setSender(this.getSender());
        copy.setRecipient(this.getRecipient());
        copy.setCc(this.getCc());
        copy.setBcc(this.getBcc());
        return copy;
    }

}
