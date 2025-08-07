package com.content_management_system.emailservice.service;

import com.content_management_system.emailservice.dto.EmailRequest;

public interface EmailService {
    void sendSimpleMessage(EmailRequest emailRequest);
}
