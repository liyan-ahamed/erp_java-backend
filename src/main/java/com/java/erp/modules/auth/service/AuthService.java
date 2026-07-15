package com.java.erp.modules.auth.service;

import com.java.erp.exception.BadRequestException;
import com.java.erp.exception.ConflictException;
import com.java.erp.exception.ResourceNotFoundException;
import com.java.erp.modules.auth.dto.request.LoginRequest;
import com.java.erp.modules.auth.dto.request.RegisterRequest;
import com.java.erp.modules.auth.dto.response.AuthResponse;
import com.java.erp.modules.auth.dto.response.UserResponse;
import com.java.erp.modules.auth.entity.Role;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.RoleRepository;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.security.CustomUserDetails;
import com.java.erp.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service handling authentication operations: login and registration.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String DEFAULT_ROLE = "VIEWER";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Authenticate a user by email and return JWT tokens.
     */
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        logger.info("User '{}' logged in successfully", loginRequest.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                roles
        );
    }

    /**
     * Register a new user.
     */
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        // Check for duplicate email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException("User", "email", registerRequest.getEmail());
        }

        // Create user entity
        User user = new User(
                registerRequest.getName(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );
        user.setPhone(registerRequest.getPhone());

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            for (String roleName : registerRequest.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                roles.add(role);
            }
        } else {
            // Default to VIEWER role
            Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                    .orElseThrow(() -> new BadRequestException(
                            "Default role " + DEFAULT_ROLE + " not found. Please run database migrations."));
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully", savedUser.getEmail());

        Set<String> roleNames = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.isActive(),
                roleNames
        );
    }
}
