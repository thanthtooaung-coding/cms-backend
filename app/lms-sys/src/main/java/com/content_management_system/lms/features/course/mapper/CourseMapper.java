package com.content_management_system.lms.features.course.mapper;

import com.content_management_system.lms.features.course.dto.CourseResponse;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.Lesson;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.entity.Rating;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseResponse toResponse(Course course) {
        return toResponse(course, null, null);
    }

    public static CourseResponse toResponse(Course course, Integer instructorTotalCourses, Integer instructorTotalStudents) {
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
                    .totalCourses(instructorTotalCourses)
                    .totalStudents(instructorTotalStudents)
                    .build();
        }

        // Map modules with lessons
        List<CourseResponse.ModuleInfo> moduleInfos = new ArrayList<>();
        if (course.getModules() != null) {
            moduleInfos = course.getModules().stream()
                    .map(CourseMapper::mapModule)
                    .collect(Collectors.toList());
        }

        // Calculate rating
        CourseResponse.RatingInfo ratingInfo = calculateRating(course);

        // Count enrollments
        Integer totalEnrolledStudents = course.getEnrollments() != null 
                ? course.getEnrollments().size() 
                : 0;

        // Generate whatYouWillLearn from description (placeholder - can be enhanced later)
        List<CourseResponse.WhatYouWillLearnItem> whatYouWillLearn = generateWhatYouWillLearn(course);

        // Generate requirements (placeholder - can be enhanced later)
        List<CourseResponse.RequirementItem> requirements = generateRequirements(course);

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .status(course.getStatus())
                .durationDayCount(course.getDurationDayCount())
                .category(categoryInfo)
                .instructor(instructorInfo)
                .modules(moduleInfos)
                .rating(ratingInfo)
                .totalEnrolledStudents(totalEnrolledStudents)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .whatYouWillLearn(whatYouWillLearn)
                .requirements(requirements)
                .build();
    }

    private static CourseResponse.ModuleInfo mapModule(Module module) {
        List<CourseResponse.LessonInfo> lessonInfos = new ArrayList<>();
        if (module.getLessons() != null) {
            lessonInfos = module.getLessons().stream()
                    .map(CourseMapper::mapLesson)
                    .collect(Collectors.toList());
        }

        return CourseResponse.ModuleInfo.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .lessons(lessonInfos)
                .build();
    }

    private static CourseResponse.LessonInfo mapLesson(Lesson lesson) {
        return CourseResponse.LessonInfo.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .materialType(lesson.getMaterialType() != null ? lesson.getMaterialType().name() : null)
                .build();
    }

    private static CourseResponse.RatingInfo calculateRating(Course course) {
        if (course.getRatings() == null || course.getRatings().isEmpty()) {
            return CourseResponse.RatingInfo.builder()
                    .averageRating(0.0)
                    .totalRatings(0)
                    .build();
        }

        // Calculate average from ratingCount values
        double sum = course.getRatings().stream()
                .filter(r -> r.getRatingCount() != null)
                .mapToInt(Rating::getRatingCount)
                .sum();
        
        int count = (int) course.getRatings().stream()
                .filter(r -> r.getRatingCount() != null)
                .count();

        double average = count > 0 ? sum / count : 0.0;
        
        // Normalize to 0-5 scale if needed (assuming ratingCount is 1-5)
        if (average > 5.0) {
            average = 5.0;
        }

        return CourseResponse.RatingInfo.builder()
                .averageRating(average)
                .totalRatings(count)
                .build();
    }

    private static List<CourseResponse.WhatYouWillLearnItem> generateWhatYouWillLearn(Course course) {
        // Placeholder: Generate from description or return empty list
        // In the future, this could be a separate entity/field
        List<CourseResponse.WhatYouWillLearnItem> items = new ArrayList<>();
        
        // For now, return empty list or generate from description
        // You can enhance this later to extract key points from description
        return items;
    }

    private static List<CourseResponse.RequirementItem> generateRequirements(Course course) {
        // Placeholder: Generate requirements or return empty list
        // In the future, this could be a separate entity/field
        List<CourseResponse.RequirementItem> items = new ArrayList<>();
        
        // For now, return empty list
        // You can enhance this later to have actual requirements
        return items;
    }
}