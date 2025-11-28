package com.content_management_system.lms.features.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitQuizRequest {
    private Long quizId;
    private List<AnswerSubmission> answers;

    @Data
    public static class AnswerSubmission {
        private Long questionId;
        private Long answerId;
    }
}

