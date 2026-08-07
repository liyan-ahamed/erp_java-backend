package com.java.erp.security;

import com.java.erp.modules.auth.entity.Permission;
import com.java.erp.modules.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom UserDetails implementation that wraps the User entity
 * and provides authorities from both roles AND permissions.
 * Uses email as the principal identifier (Spring Security's getUsername() returns email).
 *
 * Authorities include:
 * - Role-based: ROLE_HOD, ROLE_STAFF (from user.roles)
 * - Permission-based: CREATE_SCHEDULE, MANAGE_USERS, etc. (from role.permissions)
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = user.isActive();

        // Collect both role authorities and permission authorities
        Set<GrantedAuthority> authoritySet = new HashSet<>();

        user.getRoles().forEach(role -> {
            // Add role authority (e.g., ROLE_HOD)
            authoritySet.add(new SimpleGrantedAuthority(role.getName()));

            // Add permission authorities (e.g., CREATE_SCHEDULE, MANAGE_USERS)
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission ->
                        authoritySet.add(new SimpleGrantedAuthority(permission.getName()))
                );
            }
        });

        this.authorities = authoritySet;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns email as the principal identifier used by Spring Security.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
