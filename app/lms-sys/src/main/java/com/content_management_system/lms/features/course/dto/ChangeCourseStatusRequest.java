package com.content_management_system.lms.features.course.dto;

import com.content_management_system.lms.shared.constants.CourseStatus;
import lombok.Data;

@Data
public class ChangeCourseStatusRequest {
    private CourseStatus status;
}