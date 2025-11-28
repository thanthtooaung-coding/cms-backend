package com.content_management_system.lms.features.course.service;

import com.content_management_system.lms.features.course.dto.RelatedTopicsRequest;
import com.content_management_system.lms.features.course.dto.RelatedTopicsResponse;

public interface RelatedTopicsService {
    RelatedTopicsResponse generateRelatedTopics(RelatedTopicsRequest request);
}

