package com.content_management_system.lms.features.category.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteRequest {
    private List<Long> ids;
}

