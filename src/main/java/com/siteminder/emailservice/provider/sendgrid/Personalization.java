package com.siteminder.emailservice.provider.sendgrid;

import com.siteminder.emailservice.model.EmailUser;

import java.util.List;

/**
 * Used as the personalization object in a sendgrid request
 */
public class Personalization {
    private List<EmailUser> to;
    private List<EmailUser> cc;
    private List<EmailUser> bcc;

    public List<EmailUser> getTo() {
        return to;
    }

    public void setTo(List<EmailUser> to) {
        this.to = to;
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
}
