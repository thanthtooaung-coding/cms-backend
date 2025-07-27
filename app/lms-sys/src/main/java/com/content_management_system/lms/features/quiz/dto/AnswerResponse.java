package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerResponse {
    private Long id;
    private String answerText;
    private boolean correct;
}