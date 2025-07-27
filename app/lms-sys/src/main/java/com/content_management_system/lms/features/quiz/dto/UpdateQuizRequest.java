package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateQuizRequest {
    private String question;
    private List<UpdateAnswerOptionRequest> answerOptions;
}