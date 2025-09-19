package com.hari.lms.repository;

import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * @author Hari Parthu
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by username.
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role with pagination.
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Find users by enabled status with pagination.
     */
    Page<User> findByEnabled(Boolean enabled, Pageable pageable);

    /**
     * Find users by role and enabled status with pagination.
     */
    Page<User> findByRoleAndEnabled(Role role, Boolean enabled, Pageable pageable);

    /**
     * Search users by username or email containing the search term.
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByUsernameOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count users by role.
     */
    long countByRole(Role role);

    /**
     * Count enabled users.
     */
    long countByEnabled(Boolean enabled);
}