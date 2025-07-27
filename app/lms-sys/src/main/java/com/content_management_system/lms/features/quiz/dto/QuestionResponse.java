package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private List<AnswerResponse> answers;
}