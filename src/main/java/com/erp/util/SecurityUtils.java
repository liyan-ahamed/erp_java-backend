package com.erp.util;

import com.erp.security.CustomUserDetails;
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
     * Get the currently authenticated user's username.
     *
     * @return the username, or null if not authenticated
     */
    public static String getCurrentUsername() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getUsername() : null;
    }

    /**
     * Check if the current user has a specific role.
     *
     * @param role the role name (e.g., "ROLE_ADMIN")
     * @return true if the user has the role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
