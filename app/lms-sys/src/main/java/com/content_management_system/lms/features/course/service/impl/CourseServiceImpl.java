package com.content_management_system.lms.features.course.service.impl;

import com.content_management_system.lms.features.course.dto.ChangeCourseStatusRequest;
import com.content_management_system.lms.features.course.dto.CourseLessonResponse;
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
import com.content_management_system.lms.features.enrollment.repository.EnrollmentRepository;
import com.content_management_system.lms.shared.repository.CategoryRepository;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.LessonRepository;
import com.content_management_system.lms.shared.repository.ModuleRepository;
import com.content_management_system.lms.shared.repository.QuizRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import com.content_management_system.lms.shared.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.content_management_system.lms.shared.entity.Module;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final EnrollmentRepository enrollmentRepository;
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
    public List<CourseResponse> findPublishedCourses(Long tenantId) {
        // Public endpoint for students - only show published courses from their tenant
        // If tenantId is not provided, return empty list (students must have a tenant)
        if (tenantId == null) {
            return new java.util.ArrayList<>();
        }
        
        List<Course> courses = courseRepository.findAllPublishedByTenant(CourseStatus.Published, tenantId);
        
        return courses.stream()
                .map(CourseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id, Long userId) {
        // Fetch course with modules
        Course course = courseRepository.findByIdWithModules(id)
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
        
        // Load modules with lessons separately to avoid complex JOIN FETCH issues
        if (course.getModules() != null) {
            List<Module> modulesWithLessons = moduleRepository.findAllByCourseIdWithLessons(id);
            course.getModules().clear();
            course.getModules().addAll(modulesWithLessons);
        }
        
        // Initialize other collections to avoid lazy loading issues
        if (course.getRatings() != null) {
            course.getRatings().size(); // Force initialization
        }
        if (course.getEnrollments() != null) {
            course.getEnrollments().size(); // Force initialization
        }
        
        // Calculate instructor statistics if instructor exists
        Integer instructorTotalCourses = null;
        Integer instructorTotalStudents = null;
        if (course.getInstructor() != null) {
            Long instructorId = course.getInstructor().getId();
            instructorTotalCourses = (int) courseRepository.countByInstructorId(instructorId);
            instructorTotalStudents = (int) enrollmentRepository.countDistinctStudentsByInstructorId(instructorId);
        }
        
        return CourseMapper.toResponse(course, instructorTotalCourses, instructorTotalStudents);
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

    @Override
    @Transactional(readOnly = true)
    public CourseLessonResponse getCourseLessonContent(Long courseId, Long userId) {
        Course course = courseRepository.findByIdWithModules(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Explicitly load lessons for each module
        if (course.getModules() != null) {
            for (Module module : course.getModules()) {
                module.setLessons(lessonRepository.findAllByModuleId(module.getId()));
            }
        }

        // If user is instructor or staff, verify they own this course
        if (userId != null) {
            User currentUser = securityUtil.getCurrentUser(userId);
            if (securityUtil.isInstructorOrStaff(currentUser)) {
                if (course.getInstructor() == null || !course.getInstructor().getId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("Instructors and Staff can only access their own courses");
                }
            }
        }

        // Build response with modules, lessons, and quizzes
        List<CourseLessonResponse.ModuleLessonInfo> moduleInfos = course.getModules() != null ?
                course.getModules().stream()
                        .map(module -> {
                            // Get lessons
                            List<CourseLessonResponse.LessonInfo> lessonInfos = module.getLessons() != null ?
                                    module.getLessons().stream()
                                            .map(lesson -> CourseLessonResponse.LessonInfo.builder()
                                                    .id(lesson.getId())
                                                    .title(lesson.getTitle())
                                                    .content(lesson.getContent())
                                                    .materialType(lesson.getMaterialType() != null ? lesson.getMaterialType().name() : null)
                                                    .build())
                                            .collect(Collectors.toList()) :
                                    List.of();

                            // Get quizzes for this module
                            List<CourseLessonResponse.QuizInfo> quizInfos = quizRepository.findAllByModuleIdWithSoftDelete(module.getId())
                                    .stream()
                                    .map(quiz -> CourseLessonResponse.QuizInfo.builder()
                                            .id(quiz.getId())
                                            .title(quiz.getTitle())
                                            .build())
                                    .collect(Collectors.toList());

                            return CourseLessonResponse.ModuleLessonInfo.builder()
                                    .id(module.getId())
                                    .name(module.getName())
                                    .description(module.getDescription())
                                    .lessons(lessonInfos)
                                    .quizzes(quizInfos)
                                    .build();
                        })
                        .collect(Collectors.toList()) :
                List.of();

        return CourseLessonResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .modules(moduleInfos)
                .build();
    }
}