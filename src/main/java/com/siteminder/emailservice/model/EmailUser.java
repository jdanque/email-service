package com.siteminder.emailservice.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * A basic email user with name and email
 */
public class EmailUser {

    @NotNull
    private String name;

    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return email;
    }
}
