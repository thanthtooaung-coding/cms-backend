package com.content_management_system.emailservice.controller;

import com.content_management_system.emailservice.dto.EmailRequest;
import com.content_management_system.emailservice.event.EmailSentEvent;
import com.content_management_system.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {

        final EmailSentEvent event = new EmailSentEvent(this, emailRequest);

        this.eventPublisher.publishEvent(event);

        return new ResponseEntity<>("Email request for " + emailRequest.to() + " has been accepted and is being processed.", HttpStatus.ACCEPTED);
    }
}
