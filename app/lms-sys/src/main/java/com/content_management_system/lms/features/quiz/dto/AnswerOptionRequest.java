package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerOptionRequest {
    private String answer;
    private boolean isCorrect;
}