package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class QuizResultResponse {
    private Long studentQuizId;
    private Long quizId;
    private String quizTitle;
    private Long studentId;
    private String studentName;
    private Integer score;
    private Integer totalQuestions;
    private Integer attempt;
    private OffsetDateTime submittedAt;
    private List<QuestionResult> questionResults;

    @Data
    @Builder
    public static class QuestionResult {
        private Long questionId;
        private String questionText;
        private Long selectedAnswerId;
        private String selectedAnswerText;
        private Long correctAnswerId;
        private String correctAnswerText;
        private Boolean isCorrect;
    }
}

