package com.content_management_system.lms.features.course.service.impl;

import com.content_management_system.lms.features.course.dto.ChangeCourseStatusRequest;
import com.content_management_system.lms.features.course.dto.CreateCourseRequest;
import com.content_management_system.lms.features.course.dto.CourseResponse;
import com.content_management_system.lms.features.course.dto.DeleteCoursesRequest;
import com.content_management_system.lms.features.course.dto.UpdateCourseRequest;
import com.content_management_system.lms.features.course.mapper.CourseMapper;
import com.content_management_system.lms.features.course.service.CourseService;
import com.content_management_system.lms.shared.constants.CourseStatus;
import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.CourseCategory;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.exception.UnauthorizedException;
import com.content_management_system.lms.shared.repository.CategoryRepository;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import com.content_management_system.lms.shared.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public CourseResponse create(CreateCourseRequest request, Long userId) {
        User currentUser = securityUtil.getCurrentUser(userId);
        
        // If user is instructor or staff, they can only create courses for themselves
        if (securityUtil.isInstructorOrStaff(currentUser)) {
            if (!currentUser.getId().equals(request.getInstructorId())) {
                throw new UnauthorizedException("Instructors and Staff can only create courses for themselves");
            }
        }
        
        CourseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(category);
        course.setInstructor(instructor);
        course.setDurationDayCount(request.getDurationDayCount());
        course.setStatus(CourseStatus.Pending);

        Course savedCourse = courseRepository.save(course);
        return CourseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findAll(Long tenantId, Long userId) {
        List<Course> courses;
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        if (tenantId != null) {
            // Filter courses by tenant through category
            courses = courseRepository.findAll().stream()
                    .filter(course -> course.getCategory() != null 
                            && course.getCategory().getTenant() != null
                            && course.getCategory().getTenant().getId().equals(tenantId))
                    .collect(Collectors.toList());
        } else {
            courses = courseRepository.findAll();
        }
        
        // If user is instructor or staff, filter to only show their courses
        if (currentUser != null && securityUtil.isInstructorOrStaff(currentUser)) {
            courses = courses.stream()
                    .filter(course -> course.getInstructor() != null 
                            && course.getInstructor().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }
        
        return courses.stream()
                .map(CourseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        // If user is instructor or staff, verify they own this course
        if (userId != null) {
            User currentUser = securityUtil.getCurrentUser(userId);
            if (securityUtil.isInstructorOrStaff(currentUser)) {
                if (course.getInstructor() == null || !course.getInstructor().getId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("Instructors and Staff can only access their own courses");
                }
            }
        }
        
        return CourseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, UpdateCourseRequest request, Long userId) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        User currentUser = securityUtil.getCurrentUser(userId);
        
        // If user is instructor or staff, verify they own this course
        if (securityUtil.isInstructorOrStaff(currentUser)) {
            if (existingCourse.getInstructor() == null || !existingCourse.getInstructor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Instructors and Staff can only update their own courses");
            }
            // Instructors and Staff can only update courses for themselves
            if (!currentUser.getId().equals(request.getInstructorId())) {
                throw new UnauthorizedException("Instructors and Staff can only assign courses to themselves");
            }
        }

        CourseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));

        existingCourse.setTitle(request.getTitle());
        existingCourse.setDescription(request.getDescription());
        existingCourse.setCategory(category);
        existingCourse.setInstructor(instructor);
        existingCourse.setDurationDayCount(request.getDurationDayCount());
        existingCourse.setStatus(request.getStatus());

        Course updatedCourse = courseRepository.save(existingCourse);
        return CourseMapper.toResponse(updatedCourse);
    }
    
    @Override
    @Transactional
    public void deleteCourses(DeleteCoursesRequest request, Long userId) {
        User currentUser = securityUtil.getCurrentUser(userId);
        
        List<Course> coursesToDelete = courseRepository.findAllById(request.getIds());
        
        if (coursesToDelete.size() != request.getIds().size()) {
            throw new ResourceNotFoundException("One or more courses not found.");
        }

        // If user is instructor or staff, verify they own all courses
        if (securityUtil.isInstructorOrStaff(currentUser)) {
            for (Course course : coursesToDelete) {
                if (course.getInstructor() == null || !course.getInstructor().getId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("Instructors and Staff can only delete their own courses");
                }
            }
        }

        if (request.isForceDelete()) {
             courseRepository.deleteAllById(request.getIds());
        } else {
            courseRepository.deleteAll(coursesToDelete);
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long id, ChangeCourseStatusRequest request, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        User currentUser = securityUtil.getCurrentUser(userId);
        
        // If user is instructor or staff, verify they own this course
        if (securityUtil.isInstructorOrStaff(currentUser)) {
            if (course.getInstructor() == null || !course.getInstructor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Instructors and Staff can only change status of their own courses");
            }
        }
        
        course.setStatus(request.getStatus());
        courseRepository.save(course);
    }
}