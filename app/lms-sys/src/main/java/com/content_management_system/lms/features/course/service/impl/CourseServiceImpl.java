package com.content_management_system.lms.features.course.service.impl;

import com.content_management_system.lms.features.course.dto.ChangeCourseStatusRequest;
import com.content_management_system.lms.features.course.dto.CreateCourseRequest;
import com.content_management_system.lms.features.course.dto.CourseResponse;
import com.content_management_system.lms.features.course.dto.DeleteCoursesRequest;
import com.content_management_system.lms.features.course.dto.UpdateCourseRequest;
import com.content_management_system.lms.features.course.mapper.CourseMapper;
import com.content_management_system.lms.features.course.service.CourseService;
import com.content_management_system.lms.shared.constants.CourseStatus;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.CourseCategory;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.CategoryRepository;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        CourseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(category);
        course.setInstructor(instructor);
        course.setDurationDayCount(request.getDurationDayCount());
        course.setStatus(CourseStatus.Pending);

        Course savedCourse = courseRepository.save(course);
        return CourseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                .map(CourseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return CourseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        CourseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));

        existingCourse.setTitle(request.getTitle());
        existingCourse.setDescription(request.getDescription());
        existingCourse.setCategory(category);
        existingCourse.setInstructor(instructor);
        existingCourse.setDurationDayCount(request.getDurationDayCount());
        existingCourse.setStatus(request.getStatus());

        Course updatedCourse = courseRepository.save(existingCourse);
        return CourseMapper.toResponse(updatedCourse);
    }
    
    @Override
    @Transactional
    public void deleteCourses(DeleteCoursesRequest request) {
        List<Course> coursesToDelete = courseRepository.findAllById(request.getIds());
        
        if (coursesToDelete.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("One or more courses not found.");
        }

        if (request.isForceDelete()) {
             courseRepository.deleteAllById(request.getIds());
        } else {
            courseRepository.deleteAll(coursesToDelete);
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long id, ChangeCourseStatusRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        course.setStatus(request.getStatus());
        courseRepository.save(course);
    }
}