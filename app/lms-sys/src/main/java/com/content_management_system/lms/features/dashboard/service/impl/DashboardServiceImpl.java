package com.content_management_system.lms.features.dashboard.service.impl;

import com.content_management_system.lms.features.dashboard.dto.DashboardStatsResponse;
import com.content_management_system.lms.features.dashboard.service.DashboardService;
import com.content_management_system.lms.features.enrollment.repository.EnrollmentRepository;
import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.repository.CertificateRepository;
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
public class DashboardServiceImpl implements DashboardService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Long tenantId, Long userId) {
        User currentUser = userId != null ? securityUtil.getCurrentUser(userId) : null;
        
        if (currentUser == null) {
            throw new com.content_management_system.lms.shared.exception.UnauthorizedException("User authentication required");
        }

        // Check if user is Owner or Admin
        if (securityUtil.isAdminOrOwner(currentUser)) {
            return getOwnerStats(tenantId);
        } 
        // Check if user is Instructor or Staff
        else if (securityUtil.isInstructorOrStaff(currentUser)) {
            return getInstructorStats(tenantId, currentUser.getId());
        } 
        else {
            throw new com.content_management_system.lms.shared.exception.UnauthorizedException("Access denied. Only Owners, Admins, Instructors, and Staff can view dashboard statistics.");
        }
    }

    private DashboardStatsResponse getOwnerStats(Long tenantId) {
        // Count all courses in tenant
        long totalCourses = courseRepository.findAll().stream()
                .filter(course -> {
                    if (tenantId != null) {
                        return course.getCategory() != null 
                                && course.getCategory().getTenant() != null
                                && course.getCategory().getTenant().getId().equals(tenantId)
                                && course.getDeletedAt() == null;
                    }
                    return course.getDeletedAt() == null;
                })
                .count();

        // Count all students in tenant
        long totalStudents = userRepository.findAll().stream()
                .filter(user -> {
                    if (tenantId != null) {
                        return user.getTenant() != null 
                                && user.getTenant().getId().equals(tenantId)
                                && user.getRole() != null
                                && user.getRole().getName().equals(LmsRoleName.Student.name())
                                && user.getDeletedAt() == null;
                    }
                    return user.getRole() != null
                            && user.getRole().getName().equals(LmsRoleName.Student.name())
                            && user.getDeletedAt() == null;
                })
                .count();

        // Count all instructors in tenant
        long totalInstructors = userRepository.findAll().stream()
                .filter(user -> {
                    if (tenantId != null) {
                        return user.getTenant() != null 
                                && user.getTenant().getId().equals(tenantId)
                                && user.getRole() != null
                                && (user.getRole().getName().equals(LmsRoleName.Instructor.name())
                                    || user.getRole().getName().equals(LmsRoleName.Staff.name()))
                                && user.getDeletedAt() == null;
                    }
                    return user.getRole() != null
                            && (user.getRole().getName().equals(LmsRoleName.Instructor.name())
                                || user.getRole().getName().equals(LmsRoleName.Staff.name()))
                            && user.getDeletedAt() == null;
                })
                .count();

        // Count all enrollments in tenant
        long totalEnrollments = enrollmentRepository.findAll().stream()
                .filter(enrollment -> {
                    if (tenantId != null) {
                        return enrollment.getCourse() != null
                                && enrollment.getCourse().getCategory() != null
                                && enrollment.getCourse().getCategory().getTenant() != null
                                && enrollment.getCourse().getCategory().getTenant().getId().equals(tenantId)
                                && enrollment.getDeletedAt() == null;
                    }
                    return enrollment.getDeletedAt() == null;
                })
                .count();

        // Count all certificates in tenant
        long totalCertificates = certificateRepository.findAll().stream()
                .filter(certificate -> {
                    if (tenantId != null) {
                        return certificate.getCourse() != null
                                && certificate.getCourse().getCategory() != null
                                && certificate.getCourse().getCategory().getTenant() != null
                                && certificate.getCourse().getCategory().getTenant().getId().equals(tenantId)
                                && certificate.getDeletedAt() == null;
                    }
                    return certificate.getDeletedAt() == null;
                })
                .count();

        // Count all categories in tenant
        long totalCategories = categoryRepository.findAll().stream()
                .filter(category -> {
                    if (tenantId != null) {
                        return category.getTenant() != null
                                && category.getTenant().getId().equals(tenantId)
                                && category.getDeletedAt() == null;
                    }
                    return category.getDeletedAt() == null;
                })
                .count();

        return DashboardStatsResponse.builder()
                .totalCourses(totalCourses)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalEnrollments(totalEnrollments)
                .totalCertificates(totalCertificates)
                .totalCategories(totalCategories)
                .build();
    }

    private DashboardStatsResponse getInstructorStats(Long tenantId, Long instructorId) {
        // Get all courses for this instructor
        List<Course> courses = courseRepository.findAllByInstructorId(instructorId).stream()
                .filter(course -> {
                    if (tenantId != null) {
                        return course.getCategory() != null 
                                && course.getCategory().getTenant() != null
                                && course.getCategory().getTenant().getId().equals(tenantId)
                                && course.getDeletedAt() == null;
                    }
                    return course.getDeletedAt() == null;
                })
                .collect(Collectors.toList());

        long totalCourses = courses.size();

        // Count total students enrolled across all instructor's courses
        long totalStudents = courses.stream()
                .mapToLong(course -> enrollmentRepository.countByCourseId(course.getId()))
                .sum();

        // Count total certified students across all instructor's courses
        long totalCertificates = courses.stream()
                .mapToLong(course -> certificateRepository.countByCourseId(course.getId()))
                .sum();

        // For instructor/staff, we don't need these fields, but set them to 0 for consistency
        return DashboardStatsResponse.builder()
                .totalCourses(totalCourses)
                .totalStudents(totalStudents)
                .totalInstructors(0L) // Not applicable for instructor view
                .totalEnrollments(totalStudents) // Same as students for instructor view
                .totalCertificates(totalCertificates)
                .totalCategories(0L) // Not applicable for instructor view
                .build();
    }
}

