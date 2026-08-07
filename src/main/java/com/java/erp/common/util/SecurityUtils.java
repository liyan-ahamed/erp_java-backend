package com.java.erp.common.util;

import com.java.erp.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for security-related operations.
 * Provides static helper methods to access the current authenticated user.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Prevent instantiation
    }

    /**
     * Get the currently authenticated user's details.
     *
     * @return CustomUserDetails of the authenticated user, or null if not authenticated
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the currently authenticated user's ID.
     *
     * @return the user ID, or null if not authenticated
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getId() : null;
    }

    /**
     * Get the currently authenticated user's email.
     *
     * @return the email, or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getEmail() : null;
    }

    /**
     * Get the currently authenticated user's name.
     *
     * @return the name, or null if not authenticated
     */
    public static String getCurrentUserName() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getName() : null;
    }

    /**
     * Checks if the current user has a specific authority (role).
     *
     * @param authority the authority name (e.g., "ROLE_HOD", "ROLE_STAFF")
     * @return true if the user has the authority, false otherwise
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    /**
     * Checks if the current user has a specific permission.
     *
     * @param permission the permission name (e.g., "CREATE_SCHEDULE", "MANAGE_USERS")
     * @return true if the user has the permission, false otherwise
     */
    public static boolean hasPermission(String permission) {
        return hasAuthority(permission);
    }
}
