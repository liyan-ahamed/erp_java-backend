package com.java.erp.modules.user.service;

import com.java.erp.common.audit.Auditable;
import com.java.erp.common.response.PagedResponse;
import com.java.erp.exception.BadRequestException;
import com.java.erp.exception.ConflictException;
import com.java.erp.exception.ResourceNotFoundException;
import com.java.erp.modules.auth.entity.Permission;
import com.java.erp.modules.auth.entity.Role;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.RoleRepository;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.user.dto.request.CreateUserRequest;
import com.java.erp.modules.user.dto.request.UpdateUserRequest;
import com.java.erp.modules.user.dto.response.UserDetailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for comprehensive user management operations.
 * Handles CRUD, role assignment, and activation status.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String DEFAULT_ROLE = "ROLE_STAFF";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user.
     */
    @Transactional
    @Auditable(action = "CREATE", module = "USER_MANAGEMENT", entityType = "User")
    public UserDetailResponse createUser(CreateUserRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        // Check for duplicate username if provided
        if (StringUtils.hasText(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("User", "username", request.getUsername());
        }

        // Check for duplicate staff code if provided
        if (StringUtils.hasText(request.getStaffCode()) && userRepository.existsByStaffCode(request.getStaffCode())) {
            throw new ConflictException("User", "staffCode", request.getStaffCode());
        }

        // Create user entity
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        if (StringUtils.hasText(request.getUsername())) {
            user.setUsername(request.getUsername());
        }
        user.setPhone(request.getPhone());
        user.setStaffCode(request.getStaffCode());
        user.setDesignation(request.getDesignation());
        user.setActive(true);

        // Assign roles
        assignRolesToUser(user, request.getRoles());

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser.getEmail());

        return toUserDetailResponse(savedUser);
    }

    /**
     * Update an existing user.
     */
    @Transactional
    @Auditable(action = "UPDATE", module = "USER_MANAGEMENT", entityType = "User")
    public UserDetailResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update fields if provided
        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        if (StringUtils.hasText(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ConflictException("User", "username", request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("User", "email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getStaffCode() != null && !request.getStaffCode().equals(user.getStaffCode())) {
            if (userRepository.existsByStaffCode(request.getStaffCode())) {
                throw new ConflictException("User", "staffCode", request.getStaffCode());
            }
            user.setStaffCode(request.getStaffCode());
        }

        if (request.getDesignation() != null) {
            user.setDesignation(request.getDesignation());
        }

        if (request.getRoles() != null) {
            assignRolesToUser(user, request.getRoles());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getEmail());

        return toUserDetailResponse(updatedUser);
    }

    /**
     * Get user details by ID.
     */
    @Transactional(readOnly = true)
    public UserDetailResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toUserDetailResponse(user);
    }

    /**
     * Get paginated list of users with optional filtering.
     */
    @Transactional(readOnly = true)
    public PagedResponse<UserDetailResponse> getAllUsers(int page, int size, String search, Boolean active) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> usersPage;

        if (StringUtils.hasText(search)) {
            usersPage = userRepository.searchWithActiveFilter(search, active, pageable);
        } else {
            usersPage = userRepository.findAllWithActiveFilter(active, pageable);
        }

        List<UserDetailResponse> content = usersPage.getContent().stream()
                .map(this::toUserDetailResponse)
                .collect(Collectors.toList());

        return PagedResponse.of(content, usersPage);
    }

    /**
     * Deactivate a user.
     */
    @Transactional
    @Auditable(action = "DEACTIVATE", module = "USER_MANAGEMENT", entityType = "User")
    public UserDetailResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setActive(false);
        User savedUser = userRepository.save(user);
        logger.info("User deactivated: {}", savedUser.getEmail());
        
        return toUserDetailResponse(savedUser);
    }

    /**
     * Reactivate a user.
     */
    @Transactional
    @Auditable(action = "REACTIVATE", module = "USER_MANAGEMENT", entityType = "User")
    public UserDetailResponse reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setActive(true);
        User savedUser = userRepository.save(user);
        logger.info("User reactivated: {}", savedUser.getEmail());
        
        return toUserDetailResponse(savedUser);
    }

    /**
     * Helper method to assign roles to a user.
     */
    private void assignRolesToUser(User user, Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                roles.add(role);
            }
        } else {
            // Default to ROLE_STAFF role
            Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                    .orElseThrow(() -> new BadRequestException(
                            "Default role " + DEFAULT_ROLE + " not found. Please run database migrations."));
            roles.add(defaultRole);
        }
        user.setRoles(roles);
    }

    /**
     * Map a User entity to UserDetailResponse DTO.
     */
    private UserDetailResponse toUserDetailResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissionNames = new HashSet<>();
        for (Role role : user.getRoles()) {
            if (role.getPermissions() != null) {
                for (Permission permission : role.getPermissions()) {
                    permissionNames.add(permission.getName());
                }
            }
        }

        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getStaffCode(),
                user.getPhone(),
                user.getDesignation(),
                user.isActive(),
                roleNames,
                permissionNames,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
