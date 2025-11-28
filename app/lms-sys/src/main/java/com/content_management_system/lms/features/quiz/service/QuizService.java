package com.content_management_system.lms.features.quiz.service;

import com.content_management_system.lms.features.quiz.dto.CreateQuizRequest;
import com.content_management_system.lms.features.quiz.dto.DeleteQuizRequest;
import com.content_management_system.lms.features.quiz.dto.QuizResponse;
import com.content_management_system.lms.features.quiz.dto.QuizResultResponse;
import com.content_management_system.lms.features.quiz.dto.QuizSubmissionHistoryResponse;
import com.content_management_system.lms.features.quiz.dto.SubmitQuizRequest;
import com.content_management_system.lms.features.quiz.dto.UpdateQuizRequest;

import java.util.List;

public interface QuizService {
    void create(CreateQuizRequest request);

    List<QuizResponse> getAllByModuleId(Long moduleId);

    QuizResponse update(Long id, UpdateQuizRequest request);

    void delete(DeleteQuizRequest request);

    QuizResponse getById(Long id);

    QuizResultResponse submitQuiz(SubmitQuizRequest request, Long userId);

    QuizResultResponse getQuizResult(Long studentQuizId, Long userId);

    QuizSubmissionHistoryResponse getSubmissionHistory(Long quizId, Long userId);

    QuizResponse getQuizForRetake(Long quizId, Long userId);

    QuizResultResponse getLastSubmissionWithAnswers(Long quizId, Long userId);
    
    void deleteAllSubmissionsExceptLatest(Long quizId, Long userId);
}
