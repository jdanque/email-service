package com.siteminder.emailservice.controller;

import com.siteminder.emailservice.exception.EmailServiceException;
import com.siteminder.emailservice.model.Email;
import com.siteminder.emailservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Main controller for the service that handles sending email
 */
@RestController
@RequestMapping("email")
public class EmailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    /**
     * Sends the email asynchronously thru the email service
     * @param request the email
     * @return {@link HttpStatus#CREATED} upon submission of the email
     */
    @PostMapping(path = "/send", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendEmail(@Valid @RequestBody Email request){
        try{
            emailService.send(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }catch(EmailServiceException e){
            LOGGER.error("Unable to send email.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
