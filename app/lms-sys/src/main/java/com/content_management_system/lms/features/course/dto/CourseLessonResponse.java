package com.content_management_system.lms.features.course.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CourseLessonResponse {
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private List<ModuleLessonInfo> modules;

    @Data
    @Builder
    public static class ModuleLessonInfo {
        private Long id;
        private String name;
        private String description;
        private List<LessonInfo> lessons;
        private List<QuizInfo> quizzes;
    }

    @Data
    @Builder
    public static class LessonInfo {
        private Long id;
        private String title;
        private String content;
        private String materialType;
    }

    @Data
    @Builder
    public static class QuizInfo {
        private Long id;
        private String title;
    }
}

