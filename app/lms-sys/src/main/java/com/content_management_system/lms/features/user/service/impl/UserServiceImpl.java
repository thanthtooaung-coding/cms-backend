package com.content_management_system.lms.features.user.service.impl;

import com.content_management_system.lms.features.user.dto.ChangePasswordRequest;
import com.content_management_system.lms.features.user.dto.CreateUserRequest;
import com.content_management_system.lms.features.user.dto.StudentRegistrationRequest;
import com.content_management_system.lms.features.user.dto.UpdateProfileRequest;
import com.content_management_system.lms.features.user.dto.UpdateUserRequest;
import com.content_management_system.lms.features.user.dto.UserResponse;
import com.content_management_system.lms.features.user.mapper.UserMapper;
import com.content_management_system.lms.features.user.service.UserService;
import com.content_management_system.lms.shared.constants.LmsRoleName;
import com.content_management_system.lms.shared.exception.BadRequestException;
import com.content_management_system.lms.shared.entity.Role;
import com.content_management_system.lms.shared.entity.Tenant;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.exception.UnauthorizedException;
import com.content_management_system.lms.features.enrollment.repository.EnrollmentRepository;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.RoleRepository;
import com.content_management_system.lms.shared.repository.TenantRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRegistrationDate(OffsetDateTime.now());
        user.setRole(role);
        user.setTenant(tenant);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse registerStudent(StudentRegistrationRequest request) {
        // Find Student role
        Role studentRole = roleRepository.findByName(LmsRoleName.Student.name())
                .orElseThrow(() -> new ResourceNotFoundException("Student role not found. Please contact administrator."));

        // Validate tenant
        if (request.getTenantId() == null) {
            throw new BadRequestException("Tenant ID is required for student registration");
        }

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));

        // Check if username already exists
        if (userRepository.findByUsernameAndTenantId(request.getUsername(), request.getTenantId()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail() != null && user.getEmail().equals(request.getEmail()))) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRegistrationDate(OffsetDateTime.now());
        user.setRole(studentRole); // Always assign Student role
        user.setTenant(tenant);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll(String roleName, Long tenantId) {
        List<User> users;
        
        if (tenantId != null) {
            // Filter by tenant
            if (roleName != null && !roleName.isEmpty()) {
                users = userRepository.findAllByRoleName(roleName).stream()
                        .filter(user -> user.getTenant() != null && user.getTenant().getId().equals(tenantId))
                        .collect(Collectors.toList());
            } else {
                users = userRepository.findAll().stream()
                        .filter(user -> user.getTenant() != null && user.getTenant().getId().equals(tenantId))
                        .collect(Collectors.toList());
            }
        } else {
            // No tenant filter
            if (roleName != null && !roleName.isEmpty()) {
                users = userRepository.findAllByRoleName(roleName);
            } else {
                users = userRepository.findAll();
            }
        }
        
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Calculate instructor statistics if user is an instructor or staff
        Integer totalCourses = null;
        Integer totalStudents = null;
        if (user.getRole() != null && 
            (user.getRole().getName().equals("Instructor") || user.getRole().getName().equals("Staff"))) {
            totalCourses = (int) courseRepository.countByInstructorId(user.getId());
            totalStudents = (int) enrollmentRepository.countDistinctStudentsByInstructorId(user.getId());
        }
        
        return UserMapper.toResponse(user, totalCourses, totalStudents);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getRoleId() != null) {
            Role newRole = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));
            existingUser.setRole(newRole);
        }

        existingUser.setName(request.getName());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(String username, String password, Long tenantId) {
        User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Only update fields that are provided and not email/username
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User updatedUser = userRepository.save(user);
        
        // Calculate instructor statistics if user is an instructor or staff
        Integer totalCourses = null;
        Integer totalStudents = null;
        if (updatedUser.getRole() != null && 
            (updatedUser.getRole().getName().equals("Instructor") || updatedUser.getRole().getName().equals("Staff"))) {
            totalCourses = (int) courseRepository.countByInstructorId(updatedUser.getId());
            totalStudents = (int) enrollmentRepository.countDistinctStudentsByInstructorId(updatedUser.getId());
        }
        
        return UserMapper.toResponse(updatedUser, totalCourses, totalStudents);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new BadRequestException("New password cannot be empty");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}