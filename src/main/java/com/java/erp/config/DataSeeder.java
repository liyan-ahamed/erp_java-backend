package com.java.erp.config;

import com.java.erp.modules.auth.entity.Role;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.RoleRepository;
import com.java.erp.modules.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds initial users into the database on application startup.
 * Only creates users if they do not already exist.
 * Passwords are properly BCrypt-encoded using the application's PasswordEncoder.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUser(
                "hod@department.com",
                "dr.kumar",
                "Dr Kumar",
                "password123",
                "ST001",
                "9876543210",
                "HOD",
                User.DeptRole.HOD,
                "ROLE_HOD"
        );

        seedUser(
                "staff@department.com",
                "staff.user",
                "Staff User",
                "password123",
                "ST002",
                "9876543211",
                "STAFF",
                User.DeptRole.STAFF,
                "ROLE_STAFF"
        );
    }

    private void seedUser(String email, String username, String name,
                          String rawPassword, String staffCode, String phone,
                          String designation, User.DeptRole deptRole, String roleName) {
        if (userRepository.findByEmail(email).isPresent()) {
            logger.info("Seed user '{}' already exists, skipping", email);
            return;
        }

        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            logger.warn("Role '{}' not found, cannot seed user '{}'", roleName, email);
            return;
        }

        User user = new User(username, name, email, passwordEncoder.encode(rawPassword));
        user.setStaffCode(staffCode);
        user.setPhone(phone);
        user.setDesignation(designation);
        user.setDeptRole(deptRole);
        user.setActive(true);
        user.addRole(role);

        userRepository.save(user);
        logger.info("Seeded user '{}' with role '{}'", email, roleName);
    }
}
