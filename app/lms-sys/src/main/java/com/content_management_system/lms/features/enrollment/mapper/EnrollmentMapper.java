package com.content_management_system.lms.features.enrollment.mapper;

import com.content_management_system.lms.features.enrollment.dto.EnrollmentResponse;
import com.content_management_system.lms.shared.entity.Enrollment;

public class EnrollmentMapper {

    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        EnrollmentResponse.StudentInfo studentInfo = null;
        if (enrollment.getStudent() != null) {
            studentInfo = EnrollmentResponse.StudentInfo.builder()
                    .id(enrollment.getStudent().getId())
                    .name(enrollment.getStudent().getName())
                    .email(enrollment.getStudent().getEmail())
                    .build();
        }

        EnrollmentResponse.CourseInfo courseInfo = null;
        EnrollmentResponse.CategoryInfo categoryInfo = null;
        if (enrollment.getCourse() != null) {
            courseInfo = EnrollmentResponse.CourseInfo.builder()
                    .id(enrollment.getCourse().getId())
                    .title(enrollment.getCourse().getTitle())
                    .build();

            if (enrollment.getCourse().getCategory() != null) {
                categoryInfo = EnrollmentResponse.CategoryInfo.builder()
                        .id(enrollment.getCourse().getCategory().getId())
                        .name(enrollment.getCourse().getCategory().getName())
                        .build();
            }
        }

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .student(studentInfo)
                .course(courseInfo)
                .category(categoryInfo)
                .build();
    }
}