package com.content_management_system.lms.features.course.controller;

import com.content_management_system.lms.features.course.dto.ChangeCourseStatusRequest;
import com.content_management_system.lms.features.course.dto.CourseLessonResponse;
import com.content_management_system.lms.features.course.dto.CreateCourseRequest;
import com.content_management_system.lms.features.course.dto.CourseResponse;
import com.content_management_system.lms.features.course.dto.DeleteCoursesRequest;
import com.content_management_system.lms.features.course.dto.RelatedTopicsRequest;
import com.content_management_system.lms.features.course.dto.RelatedTopicsResponse;
import com.content_management_system.lms.features.course.dto.UpdateCourseRequest;
import com.content_management_system.lms.features.course.service.CourseService;
import com.content_management_system.lms.features.course.service.RelatedTopicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final RelatedTopicsService relatedTopicsService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @RequestBody CreateCourseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        CourseResponse response = courseService.create(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(required = false) Long tenantId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        List<CourseResponse> responses = courseService.findAll(tenantId, userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public")
    public ResponseEntity<List<CourseResponse>> getPublishedCourses(
            @RequestParam(required = false) Long tenantId) {
        List<CourseResponse> responses = courseService.findPublishedCourses(tenantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        CourseResponse response = courseService.findById(id, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody UpdateCourseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        CourseResponse response = courseService.update(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCourses(
            @RequestBody DeleteCoursesRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        courseService.deleteCourses(request, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeCourseStatus(
            @PathVariable Long id,
            @RequestBody ChangeCourseStatusRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        courseService.changeStatus(id, request, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/related-topics")
    public ResponseEntity<RelatedTopicsResponse> generateRelatedTopics(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        CourseResponse course = courseService.findById(id, userId);
        
        RelatedTopicsRequest request = new RelatedTopicsRequest();
        request.setCourseTitle(course.getTitle());
        request.setCourseDescription(course.getDescription());
        request.setCategory(course.getCategory() != null ? course.getCategory().getName() : "General");
        
        // Extract lesson content from modules
        if (course.getModules() != null) {
            List<RelatedTopicsRequest.LessonContent> lessons = course.getModules().stream()
                    .flatMap(module -> module.getLessons() != null ? module.getLessons().stream() : java.util.stream.Stream.empty())
                    .map(lesson -> {
                        RelatedTopicsRequest.LessonContent lessonContent = new RelatedTopicsRequest.LessonContent();
                        lessonContent.setTitle(lesson.getTitle());
                        lessonContent.setContent(lesson.getContent());
                        lessonContent.setMaterialType(lesson.getMaterialType());
                        return lessonContent;
                    })
                    .collect(java.util.stream.Collectors.toList());
            request.setLessons(lessons);
        }
        
        RelatedTopicsResponse response = relatedTopicsService.generateRelatedTopics(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/lesson-content")
    public ResponseEntity<CourseLessonResponse> getCourseLessonContent(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        CourseLessonResponse response = courseService.getCourseLessonContent(id, userId);
        return ResponseEntity.ok(response);
    }
}