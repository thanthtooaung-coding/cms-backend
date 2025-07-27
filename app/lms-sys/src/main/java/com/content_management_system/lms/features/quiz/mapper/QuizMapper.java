package com.content_management_system.lms.features.quiz.mapper;

import com.content_management_system.lms.features.quiz.dto.AnswerResponse;
import com.content_management_system.lms.features.quiz.dto.QuestionResponse;
import com.content_management_system.lms.features.quiz.dto.QuizResponse;
import com.content_management_system.lms.shared.entity.Answer;
import com.content_management_system.lms.shared.entity.Question;
import com.content_management_system.lms.shared.entity.Quiz;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuizMapper {

    public static QuizResponse toQuizResponse(Quiz quiz) {
        if (quiz == null) {
            return null;
        }

        List<QuestionResponse> questions = quiz.getQuestions() != null ?
                quiz.getQuestions().stream()
                        .map(QuizMapper::toQuestionResponse)
                        .toList() :
                Collections.emptyList();

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .moduleId(quiz.getModule() != null ? quiz.getModule().getId() : null)
                .questions(questions)
                .build();
    }

    public static List<QuizResponse> toQuizResponseList(List<Quiz> quizzes) {
        if (quizzes == null) {
            return Collections.emptyList();
        }
        return quizzes.stream()
                .map(QuizMapper::toQuizResponse)
                .toList();
    }

    public static QuestionResponse toQuestionResponse(Question question) {
        if (question == null) {
            return null;
        }

        List<AnswerResponse> answers = question.getAnswers() != null ?
                question.getAnswers().stream()
                        .map(QuizMapper::toAnswerResponse)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .answers(answers)
                .build();
    }

    public static AnswerResponse toAnswerResponse(Answer answer) {
        if (answer == null) {
            return null;
        }
        return AnswerResponse.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .correct(answer.getCorrect() != null ? answer.getCorrect() : false)
                .build();
    }
}
