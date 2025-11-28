package com.content_management_system.lms.features.course.controller;

import com.content_management_system.lms.features.course.dto.ChangeCourseStatusRequest;
import com.content_management_system.lms.features.course.dto.CreateCourseRequest;
import com.content_management_system.lms.features.course.dto.CourseResponse;
import com.content_management_system.lms.features.course.dto.DeleteCoursesRequest;
import com.content_management_system.lms.features.course.dto.UpdateCourseRequest;
import com.content_management_system.lms.features.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

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
}