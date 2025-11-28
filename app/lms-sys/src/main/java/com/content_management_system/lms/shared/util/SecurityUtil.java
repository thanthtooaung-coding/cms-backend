package com.content_management_system.lms.shared.util;

import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.repository.UserRepository;
import com.content_management_system.lms.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    /**
     * Get user by ID and verify they exist
     */
    public User getCurrentUser(Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("User ID is required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    /**
     * Check if user has a specific role (by enum)
     */
    public boolean hasRole(User user, LmsRoleName role) {
        return user.getRole() != null && user.getRole().getName() != null 
                && user.getRole().getName().equals(role.name());
    }

    /**
     * Check if user has a specific role (by string)
     */
    public boolean hasRole(User user, String roleName) {
        return user.getRole() != null && user.getRole().getName() != null 
                && user.getRole().getName().equals(roleName);
    }

    /**
     * Check if user is an instructor
     */
    public boolean isInstructor(User user) {
        return hasRole(user, LmsRoleName.Instructor.name());
    }

    /**
     * Check if user is staff
     */
    public boolean isStaff(User user) {
        return hasRole(user, LmsRoleName.Staff.name());
    }

    /**
     * Check if user is instructor or staff (both have same permissions)
     */
    public boolean isInstructorOrStaff(User user) {
        return isInstructor(user) || isStaff(user);
    }

    /**
     * Check if user is admin or owner
     */
    public boolean isAdminOrOwner(User user) {
        return hasRole(user, LmsRoleName.Admin.name()) || hasRole(user, LmsRoleName.Owner.name());
    }

    /**
     * Verify user has required role, throw exception if not
     */
    public void requireRole(User user, LmsRoleName requiredRole) {
        if (!hasRole(user, requiredRole)) {
            throw new UnauthorizedException("User does not have required role: " + requiredRole);
        }
    }
}


