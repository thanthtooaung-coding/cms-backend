package com.content_management_system.lms.features.course.dto;

import lombok.Data;
import java.util.List;

@Data
public class RelatedTopicsResponse {
    private List<Topic> topics;

    @Data
    public static class Topic {
        private Integer id;
        private String name;
    }
}

