package com.hari.lms.repository;

import com.hari.lms.entity.Course;
import com.hari.lms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Course entity operations.
 * 
 * @author Hari Parthu
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Find courses by approval status with pagination.
     */
    Page<Course> findByIsApproved(Boolean isApproved, Pageable pageable);

    /**
     * Find courses by instructor with pagination.
     */
    Page<Course> findByInstructor(User instructor, Pageable pageable);

    /**
     * Find courses by instructor and approval status with pagination.
     */
    Page<Course> findByInstructorAndIsApproved(User instructor, Boolean isApproved, Pageable pageable);

    /**
     * Find courses by instructor ID with pagination.
     */
    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    /**
     * Search courses by title containing the search term.
     */
    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Course> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search published courses by title or description.
     */
    @Query("SELECT c FROM Course c WHERE c.isApproved = true AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Course> searchPublishedCourses(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find courses that a student is not enrolled in.
     */
    @Query("SELECT c FROM Course c WHERE c.isApproved = true AND " +
            "c.id NOT IN (SELECT e.course.id FROM Enrollment e WHERE e.student.id = :studentId)")
    Page<Course> findAvailableCoursesForStudent(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * Find courses that a student is enrolled in.
     */
    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.student.id = :studentId")
    Page<Course> findEnrolledCoursesByStudent(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * Count courses by approval status.
     */
    long countByIsApproved(Boolean isApproved);

    /**
     * Count courses by instructor.
     */
    long countByInstructor(User instructor);

    /**
     * Count courses by instructor ID.
     */
    long countByInstructorId(Long instructorId);
}