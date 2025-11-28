package com.content_management_system.lms.features.course.service;

import com.content_management_system.lms.features.course.dto.*;

import java.util.List;

public interface CourseService {

    CourseResponse create(CreateCourseRequest request, Long userId);

    List<CourseResponse> findAll(Long tenantId, Long userId);

    List<CourseResponse> findPublishedCourses(Long tenantId);

    CourseResponse findById(Long id, Long userId);

    CourseResponse update(Long id, UpdateCourseRequest request, Long userId);
    
    void deleteCourses(DeleteCoursesRequest request, Long userId);

    void changeStatus(Long id, ChangeCourseStatusRequest request, Long userId);

    CourseLessonResponse getCourseLessonContent(Long courseId, Long userId);
}