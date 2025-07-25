package com.content_management_system.lms.features.course.service;

import com.content_management_system.lms.features.course.dto.*;

import java.util.List;

public interface CourseService {

    CourseResponse create(CreateCourseRequest request);

    List<CourseResponse> findAll();

    CourseResponse findById(Long id);

    CourseResponse update(Long id, UpdateCourseRequest request);
    
    void deleteCourses(DeleteCoursesRequest request);

    void changeStatus(Long id, ChangeCourseStatusRequest request);
}