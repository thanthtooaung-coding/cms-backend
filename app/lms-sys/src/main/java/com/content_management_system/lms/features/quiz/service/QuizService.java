package com.content_management_system.lms.features.quiz.service;

import com.content_management_system.lms.features.quiz.dto.CreateQuizRequest;
import com.content_management_system.lms.features.quiz.dto.DeleteQuizRequest;
import com.content_management_system.lms.features.quiz.dto.QuizResponse;
import com.content_management_system.lms.features.quiz.dto.UpdateQuizRequest;

import java.util.List;

public interface QuizService {
    void create(CreateQuizRequest request);

    List<QuizResponse> getAllByModuleId(Long moduleId);

    QuizResponse update(Long id, UpdateQuizRequest request);

    void delete(DeleteQuizRequest request);
}
