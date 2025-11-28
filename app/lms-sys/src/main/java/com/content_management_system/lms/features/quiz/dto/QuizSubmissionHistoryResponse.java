package com.content_management_system.lms.features.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class QuizSubmissionHistoryResponse {
    private Long studentQuizId;
    private Long quizId;
    private String quizTitle;
    private Integer attempt;
    private Integer score;
    private Integer totalQuestions;
    private OffsetDateTime submittedAt;
    private List<QuestionWithoutAnswer> questions;

    @Data
    @Builder
    public static class QuestionWithoutAnswer {
        private Long questionId;
        private String questionText;
        private List<AnswerOption> answerOptions; // Answers in randomized order
    }

    @Data
    @Builder
    public static class AnswerOption {
        private Long answerId;
        private String answerText;
        // Note: correct flag is NOT included to prevent cheating
    }
}

