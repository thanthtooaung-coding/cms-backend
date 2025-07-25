package com.content_management_system.lms.features.lesson.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.content_management_system.lms.features.lesson.dto.CreateLessonRequest;
import com.content_management_system.lms.features.lesson.dto.LessonResponse;
import com.content_management_system.lms.features.lesson.dto.UpdateLessonRequest;
import com.content_management_system.lms.features.lesson.mapper.LessonMapper;
import com.content_management_system.lms.features.lesson.service.LessonService;
import com.content_management_system.lms.shared.entity.Lesson;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.LessonRepository;
import com.content_management_system.lms.shared.repository.ModuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
	
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    
    @Override
    @Transactional
    public LessonResponse create(CreateLessonRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setMaterialType(request.getMaterialType());
        lesson.setModule(module);

        Lesson savedLesson = lessonRepository.save(lesson);
        return LessonMapper.toResponse(savedLesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> findAll() {
        return lessonRepository.findAll().stream()
                .map(LessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse findById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
        return LessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional
    public LessonResponse update(Long id, UpdateLessonRequest request) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));

        existingLesson.setTitle(request.getTitle());
        existingLesson.setContent(request.getContent());
        existingLesson.setMaterialType(request.getMaterialType());

        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return LessonMapper.toResponse(updatedLesson);
    }
    
    @Override
    public void deleteById(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }
}