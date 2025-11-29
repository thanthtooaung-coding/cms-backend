package com.content_management_system.lms.features.enrollment.service.impl;

import com.content_management_system.lms.features.enrollment.dto.CreateEnrollmentRequest;
import com.content_management_system.lms.features.enrollment.dto.EnrollmentResponse;
import com.content_management_system.lms.features.enrollment.dto.EnrolledCourseResponse;
import com.content_management_system.lms.features.enrollment.mapper.EnrollmentMapper;
import com.content_management_system.lms.features.enrollment.repository.EnrollmentRepository;
import com.content_management_system.lms.features.enrollment.service.EnrollmentService;
import com.content_management_system.lms.shared.constants.EnrollmentStatus;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.CourseCategory;
import com.content_management_system.lms.shared.entity.Enrollment;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.exception.UnauthorizedException;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import com.content_management_system.lms.shared.repository.ModuleRepository;
import com.content_management_system.lms.shared.repository.QuizRepository;
import com.content_management_system.lms.shared.repository.LessonRepository;
import com.content_management_system.lms.shared.repository.CertificateRepository;
import com.content_management_system.lms.shared.entity.Rating;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.entity.Lesson;
import com.content_management_system.lms.shared.entity.Quiz;
import com.content_management_system.lms.shared.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final CertificateRepository certificateRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public EnrollmentResponse create(CreateEnrollmentRequest request, Long userId) {
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        // If current user is a student and no studentId provided, use current user
        // If studentId is provided, use that (for admin/instructor enrolling others)
        Long studentIdToUse;
        if (currentUser != null && securityUtil.isStudent(currentUser)) {
            // Students can only enroll themselves
            studentIdToUse = currentUser.getId();
        } else if (request.getStudentId() != null) {
            // Use provided studentId (for admin/instructor enrolling others)
            studentIdToUse = request.getStudentId();
        } else if (currentUser != null) {
            // If no studentId provided and user is logged in, use current user
            studentIdToUse = currentUser.getId();
        } else {
            throw new IllegalStateException("Student ID is required for enrollment");
        }
        
        final Long finalStudentId = studentIdToUse;
        User student = userRepository.findById(finalStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + finalStudentId));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // If user is instructor or staff, verify they own the course
        if (currentUser != null && securityUtil.isInstructorOrStaff(currentUser)) {
            if (course.getInstructor() == null || !course.getInstructor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Instructors and Staff can only create enrollments for their own courses");
            }
        }

        CourseCategory courseCategory = course.getCategory();
        if (student.getTenant() == null || courseCategory.getTenant() == null || !Objects.equals(student.getTenant().getId(), courseCategory.getTenant().getId())) {
            throw new IllegalStateException("Operation failed: Student and Course must belong to the same tenant.");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(finalStudentId, request.getCourseId())) {
            throw new IllegalStateException("Operation failed: Student is already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(OffsetDateTime.now());
        enrollment.setStatus(EnrollmentStatus.Enrolled);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return EnrollmentMapper.toResponse(savedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findAll(Long tenantId, Long userId) {
        List<Enrollment> enrollments;
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        if (tenantId != null) {
            // Filter enrollments by tenant through student or course category
            enrollments = enrollmentRepository.findAll().stream()
                    .filter(enrollment -> {
                        User student = enrollment.getStudent();
                        Course course = enrollment.getCourse();
                        if (student != null && student.getTenant() != null 
                                && student.getTenant().getId().equals(tenantId)) {
                            return true;
                        }
                        if (course != null && course.getCategory() != null 
                                && course.getCategory().getTenant() != null
                                && course.getCategory().getTenant().getId().equals(tenantId)) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        } else {
            enrollments = enrollmentRepository.findAll();
        }
        
        // If user is instructor or staff, filter to only show enrollments for their courses
        if (currentUser != null && securityUtil.isInstructorOrStaff(currentUser)) {
            enrollments = enrollments.stream()
                    .filter(enrollment -> {
                        Course course = enrollment.getCourse();
                        return course != null && course.getInstructor() != null 
                                && course.getInstructor().getId().equals(currentUser.getId());
                    })
                    .collect(Collectors.toList());
        }
        
        return enrollments.stream()
                .map(EnrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> search(String studentEmail, Long courseId, Long categoryId, Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.search(studentEmail, courseId, categoryId);
        
        // If user is instructor or staff, filter to only show enrollments for their courses
        if (userId != null) {
            User currentUser = securityUtil.getCurrentUser(userId);
            if (securityUtil.isInstructorOrStaff(currentUser)) {
                enrollments = enrollments.stream()
                        .filter(enrollment -> {
                            Course course = enrollment.getCourse();
                            return course != null && course.getInstructor() != null 
                                    && course.getInstructor().getId().equals(currentUser.getId());
                        })
                        .collect(Collectors.toList());
            }
        }
        
        return enrollments.stream()
                .map(EnrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id, Long userId) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        
        // If user is instructor or staff, verify they own the course
        if (userId != null) {
            User currentUser = securityUtil.getCurrentUser(userId);
            if (securityUtil.isInstructorOrStaff(currentUser)) {
                Course course = enrollment.getCourse();
                if (course == null || course.getInstructor() == null 
                        || !course.getInstructor().getId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("Instructors and Staff can only delete enrollments for their own courses");
                }
            }
        }
        
        enrollmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long id, Long userId) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        
        // If user is instructor or staff, verify they own the course
        if (userId != null) {
            User currentUser = securityUtil.getCurrentUser(userId);
            if (securityUtil.isInstructorOrStaff(currentUser)) {
                Course course = enrollment.getCourse();
                if (course == null || course.getInstructor() == null 
                        || !course.getInstructor().getId().equals(currentUser.getId())) {
                    throw new UnauthorizedException("Instructors and Staff can only cancel enrollments for their own courses");
                }
            }
        }

        enrollment.setStatus(EnrollmentStatus.Dropped);
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolledCourseResponse> getEnrolledCourses(Long studentId, Long userId) {
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        // Students can only view their own enrolled courses
        if (currentUser != null && securityUtil.isStudent(currentUser) && !currentUser.getId().equals(studentId)) {
            throw new UnauthorizedException("Students can only view their own enrolled courses");
        }
        
        // Get all enrollments for the student with course details
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdWithCourseDetails(studentId);
        
        return enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    if (course == null) {
                        return null;
                    }
                    
                    // Count modules, lessons, and quizzes using separate queries
                    List<Module> modules = moduleRepository.findAllByCourseId(course.getId());
                    int totalModules = modules.size();
                    int totalLessons = 0;
                    int totalQuizzes = 0;
                    
                    for (var module : modules) {
                        List<Lesson> lessons = lessonRepository.findAllByModuleId(module.getId());
                        totalLessons += lessons.size();
                        
                        List<Quiz> quizzes = quizRepository.findAllByModuleIdWithSoftDelete(module.getId());
                        totalQuizzes += quizzes.size();
                    }
                    
                    // Calculate average rating using a separate query
                    Double averageRating = 0.0;
                    Integer totalRatings = 0;
                    // We'll use a simple approach - fetch ratings separately if needed
                    // For now, set default values since we don't have a direct count query
                    // This can be optimized later with a count query
                    
                    // Check for certificate
                    Boolean hasCertificate = false;
                    Double certificateScore = null;
                    var certificate = certificateRepository.findByStudentIdAndCourseId(studentId, course.getId()).orElse(null);
                    if (certificate != null) {
                        hasCertificate = true;
                        certificateScore = certificate.getScorePercentage() != null 
                                ? certificate.getScorePercentage().doubleValue() 
                                : null;
                    }
                    
                    return EnrolledCourseResponse.builder()
                            .enrollmentId(enrollment.getId())
                            .courseId(course.getId())
                            .courseTitle(course.getTitle())
                            .courseDescription(course.getDescription())
                            .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                            .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                            .instructorName(course.getInstructor() != null ? course.getInstructor().getName() : null)
                            .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                            .instructorEmail(course.getInstructor() != null ? course.getInstructor().getEmail() : null)
                            .enrollmentDate(enrollment.getEnrollmentDate())
                            .enrollmentStatus(enrollment.getStatus() != null ? enrollment.getStatus().name() : null)
                            .totalModules(totalModules)
                            .totalLessons(totalLessons)
                            .totalQuizzes(totalQuizzes)
                            .averageRating(averageRating)
                            .totalRatings(totalRatings)
                            .totalEnrolledStudents(course.getEnrollments() != null ? course.getEnrollments().size() : 0)
                            .courseCreatedAt(course.getCreatedAt())
                            .courseUpdatedAt(course.getUpdatedAt())
                            .courseStatus(course.getStatus() != null ? course.getStatus().name() : null)
                            .durationDayCount(course.getDurationDayCount())
                            .hasCertificate(hasCertificate)
                            .certificateScore(certificateScore)
                            .build();
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse> getEnrollmentStatistics(Long tenantId, Long userId) {
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        if (currentUser == null) {
            throw new UnauthorizedException("User authentication required");
        }

        List<com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse> statistics = new java.util.ArrayList<>();

        // Check if user is Owner
        if (securityUtil.hasRole(currentUser, com.content_management_system.lms.shared.constants.LmsRoleName.Owner.name())) {
            // Owner: Get all instructors with their courses
            List<User> instructors = userRepository.findAllByRoleName("Instructor");
            
            // Also include Staff as they can be instructors too
            List<User> staff = userRepository.findAllByRoleName("Staff");
            instructors.addAll(staff);
            
            // Remove duplicates and filter by tenant if needed
            if (tenantId != null) {
                instructors = instructors.stream()
                        .filter(instructor -> instructor.getTenant() != null 
                                && instructor.getTenant().getId().equals(tenantId))
                        .distinct()
                        .collect(Collectors.toList());
            }
            
            // Sort by instructor name
            instructors.sort((a, b) -> {
                String nameA = a.getName() != null ? a.getName() : a.getUsername();
                String nameB = b.getName() != null ? b.getName() : b.getUsername();
                return nameA.compareToIgnoreCase(nameB);
            });
            
            // For each instructor, get their courses and statistics
            for (User instructor : instructors) {
                List<Course> courses = courseRepository.findAllByInstructorId(instructor.getId()).stream()
                        .filter(course -> {
                            if (tenantId != null) {
                                return course.getCategory() != null 
                                        && course.getCategory().getTenant() != null
                                        && course.getCategory().getTenant().getId().equals(tenantId);
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
                
                for (Course course : courses) {
                    // Count enrollments for this course
                    long studentCount = enrollmentRepository.countByCourseId(course.getId());
                    
                    // Count certificates for this course
                    long certifiedCount = certificateRepository.countByCourseId(course.getId());
                    
                    statistics.add(com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse.builder()
                            .courseId(course.getId())
                            .courseName(course.getTitle())
                            .instructorId(instructor.getId())
                            .instructorName(instructor.getName() != null ? instructor.getName() : instructor.getUsername())
                            .studentCount(studentCount)
                            .certifiedStudentCount(certifiedCount)
                            .build());
                }
            }
        } else if (securityUtil.isInstructorOrStaff(currentUser)) {
            // Instructor/Staff: Get only their courses
            List<Course> courses = courseRepository.findAllByInstructorId(currentUser.getId()).stream()
                    .filter(course -> {
                        if (tenantId != null) {
                            return course.getCategory() != null 
                                    && course.getCategory().getTenant() != null
                                    && course.getCategory().getTenant().getId().equals(tenantId);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            
            for (Course course : courses) {
                // Count enrollments for this course
                long studentCount = enrollmentRepository.countByCourseId(course.getId());
                
                // Count certificates for this course
                long certifiedCount = certificateRepository.countByCourseId(course.getId());
                
                statistics.add(com.content_management_system.lms.features.enrollment.dto.EnrollmentStatisticsResponse.builder()
                        .courseId(course.getId())
                        .courseName(course.getTitle())
                        .instructorId(currentUser.getId())
                        .instructorName(currentUser.getName() != null ? currentUser.getName() : currentUser.getUsername())
                        .studentCount(studentCount)
                        .certifiedStudentCount(certifiedCount)
                        .build());
            }
        } else {
            throw new UnauthorizedException("Only Owner, Instructor, and Staff can view enrollment statistics");
        }
        
        return statistics;
    }
}