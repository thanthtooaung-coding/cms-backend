package com.content_management_system.emailservice.event;

import com.content_management_system.emailservice.dto.EmailRequest;
import org.springframework.context.ApplicationEvent;

public class EmailSentEvent extends ApplicationEvent {

    private final EmailRequest emailRequest;

    public EmailSentEvent(Object source, EmailRequest emailRequest) {
        super(source);
        this.emailRequest = emailRequest;
    }

    public EmailRequest getEmailRequest() {
        return emailRequest;
    }
}
