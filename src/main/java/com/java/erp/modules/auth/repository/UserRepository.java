package com.java.erp.modules.auth.repository;

import com.java.erp.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Count all active users (staff).
     */
    long countByActiveTrue();

    /**
     * Find all active users.
     */
    List<User> findByActiveTrue();
}
