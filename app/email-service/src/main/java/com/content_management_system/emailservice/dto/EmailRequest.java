package com.content_management_system.emailservice.dto;

public record EmailRequest(String to, String subject, String body) {}