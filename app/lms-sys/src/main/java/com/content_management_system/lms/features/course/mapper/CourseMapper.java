package com.content_management_system.lms.features.course.mapper;

import com.content_management_system.lms.features.course.dto.CourseResponse;
import com.content_management_system.lms.shared.entity.Course;

public class CourseMapper {

    public static CourseResponse toResponse(Course course) {
        CourseResponse.CategoryInfo categoryInfo = null;
        if (course.getCategory() != null) {
            categoryInfo = CourseResponse.CategoryInfo.builder()
                    .id(course.getCategory().getId())
                    .name(course.getCategory().getName())
                    .description(course.getCategory().getDescription())
                    .build();
        }

        CourseResponse.InstructorInfo instructorInfo = null;
        if (course.getInstructor() != null) {
            instructorInfo = CourseResponse.InstructorInfo.builder()
                    .id(course.getInstructor().getId())
                    .name(course.getInstructor().getName())
                    .email(course.getInstructor().getEmail())
                    .build();
        }

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(categoryInfo)
                .instructor(instructorInfo)
                .build();
    }
}