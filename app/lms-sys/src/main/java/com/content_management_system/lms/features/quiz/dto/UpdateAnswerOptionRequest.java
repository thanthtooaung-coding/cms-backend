package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAnswerOptionRequest {
    private Long id;
    private String answer;
    private Boolean isCorrect;
}
