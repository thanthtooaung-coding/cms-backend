package com.content_management_system.lms.features.course.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeleteCoursesRequest {
    private List<Long> ids;
    private boolean forceDelete = false;
}