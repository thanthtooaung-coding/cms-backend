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
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
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

    @Override
    @Transactional
    public EnrollmentResponse create(CreateEnrollmentRequest request) {
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

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
    public List<EnrollmentResponse> findAll(Long tenantId) {
        List<Enrollment> enrollments;
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
        return enrollments.stream()
                .map(EnrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> search(String studentEmail, Long courseId, Long categoryId) {
        return enrollmentRepository.search(studentEmail, courseId, categoryId).stream()
                .map(EnrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment not found with id: " + id);
        }
        enrollmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        enrollment.setStatus(EnrollmentStatus.Dropped);
        enrollmentRepository.save(enrollment);
    }
}