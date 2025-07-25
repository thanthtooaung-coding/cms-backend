package com.content_management_system.lms.features.lesson.service;

import java.util.List;

import com.content_management_system.lms.features.lesson.dto.CreateLessonRequest;
import com.content_management_system.lms.features.lesson.dto.LessonResponse;
import com.content_management_system.lms.features.lesson.dto.UpdateLessonRequest;

public interface LessonService {
    LessonResponse create(CreateLessonRequest request);

    List<LessonResponse> findAll();

    LessonResponse findById(Long id);

    LessonResponse update(Long id, UpdateLessonRequest request);
    
    void deleteById(Long id);
}