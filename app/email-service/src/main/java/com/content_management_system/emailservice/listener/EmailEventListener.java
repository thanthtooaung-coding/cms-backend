package com.content_management_system.emailservice.listener;

import com.content_management_system.emailservice.event.EmailSentEvent;
import com.content_management_system.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    @EventListener
    public void handleEmailSentEvent(EmailSentEvent event) {
        log.info("Received email send event for recipient: {}", event.getEmailRequest().to());
        try {
            emailService.sendSimpleMessage(event.getEmailRequest());
            log.info("Email successfully processed for recipient: {}", event.getEmailRequest().to());
        } catch (Exception e) {
            log.error("Failed to send email to {} after event received.", event.getEmailRequest().to(), e);
        }
    }
}
