package com.content_management_system.emailservice.service.impl;

import com.content_management_system.emailservice.dto.EmailRequest;
import com.content_management_system.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(final EmailRequest emailRequest) {
        try {
            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailRequest.to());
            message.setSubject(emailRequest.subject());
            message.setText(emailRequest.body());
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
