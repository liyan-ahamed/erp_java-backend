package com.java.erp.common.mapper;

import com.java.erp.modules.auth.dto.response.UserResponse;
import com.java.erp.modules.auth.entity.Role;
import com.java.erp.modules.auth.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for mapping User entities to response DTOs.
 * Centralizes the mapping logic that was previously duplicated
 * in AuthService and ScheduleController.
 */
public final class UserMapper {

    private UserMapper() {
        // Prevent instantiation
    }

    /**
     * Map a User entity to a UserResponse DTO.
     *
     * @param user the user entity
     * @return the user response DTO
     */
    public static UserResponse toUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getStaffCode(),
                user.getPhone(),
                user.getDesignation(),
                user.isActive(),
                roleNames
        );
    }
}
