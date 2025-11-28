package com.content_management_system.lms.features.enrollment.service.impl;

import com.content_management_system.lms.features.enrollment.dto.CreateEnrollmentRequest;
import com.content_management_system.lms.features.enrollment.dto.EnrollmentResponse;
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
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public EnrollmentResponse create(CreateEnrollmentRequest request, Long userId) {
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

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

        if (enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
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
}