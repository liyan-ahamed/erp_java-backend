package com.java.erp.modules.auth.repository;

import com.java.erp.modules.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 * Extends JpaSpecificationExecutor for dynamic filtering support.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByStaffCode(String staffCode);

    List<User> findByActiveTrue();

    long countByActiveTrue();

    /**
     * Search users by name or email containing the query string, case-insensitive.
     */
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchByNameOrEmail(@Param("search") String search, Pageable pageable);

    /**
     * Find all users with pagination and optional active filter.
     */
    @Query("SELECT u FROM User u WHERE (:active IS NULL OR u.active = :active)")
    Page<User> findAllWithActiveFilter(@Param("active") Boolean active, Pageable pageable);

    /**
     * Search users with optional active filter.
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:active IS NULL OR u.active = :active) AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchWithActiveFilter(@Param("search") String search,
                                      @Param("active") Boolean active,
                                      Pageable pageable);
}
