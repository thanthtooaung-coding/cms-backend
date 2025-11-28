package com.content_management_system.lms.features.course.dto;

import lombok.Data;
import java.util.List;

@Data
public class RelatedTopicsRequest {
    private String courseTitle;
    private String courseDescription;
    private String category;
    private List<LessonContent> lessons;

    @Data
    public static class LessonContent {
        private String title;
        private String content;
        private String materialType; // Video, Article, Quiz, etc.
    }
}

