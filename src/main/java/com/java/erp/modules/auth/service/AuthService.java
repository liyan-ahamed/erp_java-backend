package com.java.erp.modules.auth.service;

import com.java.erp.common.audit.Auditable;
import com.java.erp.common.mapper.UserMapper;

import com.java.erp.exception.AccountDeactivatedException;
import com.java.erp.exception.BadRequestException;
import com.java.erp.exception.ConflictException;
import com.java.erp.exception.ResourceNotFoundException;
import com.java.erp.exception.UnauthorizedException;
import com.java.erp.modules.auth.dto.request.LoginRequest;
import com.java.erp.modules.auth.dto.request.RefreshTokenRequest;
import com.java.erp.modules.auth.dto.request.RegisterRequest;
import com.java.erp.modules.auth.dto.response.AuthResponse;
import com.java.erp.modules.auth.dto.response.UserResponse;
import com.java.erp.modules.auth.entity.Role;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.RoleRepository;

import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.security.CustomUserDetails;
import com.java.erp.security.JwtService;
import com.java.erp.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service handling authentication operations: login, registration, token refresh.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String DEFAULT_ROLE = "ROLE_STAFF";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final HttpServletRequest httpServletRequest;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       TokenBlacklistService tokenBlacklistService,
                       HttpServletRequest httpServletRequest) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Authenticate a user by email and return JWT tokens with user info.
     */
    @Auditable(action = "LOGIN", module = "AUTH", entityType = "User")
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

        // Load full user entity for the response
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        UserResponse userResponse = UserMapper.toUserResponse(user);

        logger.info("User '{}' logged in successfully", loginRequest.getEmail());

        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    /**
     * Refresh JWT tokens using a valid refresh token.
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate the token
        if (!jwtService.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token.");
        }

        // Verify it is a REFRESH type token
        String tokenType = jwtService.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new UnauthorizedException("Invalid token type.");
        }

        // Extract email and load user
        String email = jwtService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired refresh token."));

        // Check user is still active
        if (!user.isActive()) {
            throw new AccountDeactivatedException("Your account has been deactivated.");
        }

        // Generate new tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        logger.info("Tokens refreshed for user '{}'", email);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    /**
     * Register a new user.
     */
    @Transactional
    @Auditable(action = "REGISTER", module = "AUTH", entityType = "User")
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
            // Default to ROLE_STAFF role
            Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                    .orElseThrow(() -> new BadRequestException(
                            "Default role " + DEFAULT_ROLE + " not found. Please run database migrations."));
            roles.add(defaultRole);
        }
        user.setRoles(roles);
        if (roles.stream().anyMatch(role -> "ROLE_STUDENT".equals(role.getName()))) {
            user.setDeptRole(User.DeptRole.STUDENT);
        } else if (roles.stream().anyMatch(role -> "ROLE_HOD".equals(role.getName()))) {
            user.setDeptRole(User.DeptRole.HOD);
        } else {
            user.setDeptRole(User.DeptRole.STAFF);
        }

        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully", savedUser.getEmail());

        return UserMapper.toUserResponse(savedUser);
    }

    /**
     * Retrieve the currently authenticated user's information.
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return UserMapper.toUserResponse(user);
    }

    /**
     * Logout the currently authenticated user.
     * Blacklists the current JWT token so it cannot be reused.
     */
    @Auditable(action = "LOGOUT", module = "AUTH", entityType = "User")
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            // Blacklist the current token
            String bearerToken = httpServletRequest.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                try {
                    Date expiration = jwtService.getExpirationFromToken(token);
                    tokenBlacklistService.blacklist(token, expiration.toInstant());
                } catch (Exception e) {
                    logger.warn("Failed to blacklist token during logout: {}", e.getMessage());
                }
            }
            logger.info("User '{}' logged out", authentication.getName());
        }
        SecurityContextHolder.clearContext();
    }

}
