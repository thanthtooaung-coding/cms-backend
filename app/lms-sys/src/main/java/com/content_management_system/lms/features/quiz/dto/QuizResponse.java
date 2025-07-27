package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private Long moduleId;
    private List<QuestionResponse> questions;
}