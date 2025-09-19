package com.hari.lms.repository;

import com.hari.lms.entity.Course;
import com.hari.lms.entity.Enrollment;
import com.hari.lms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Enrollment entity operations.
 * 
 * @author Hari Parthu
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Find enrollment by student and course.
     */
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    /**
     * Find enrollment by student ID and course ID.
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Check if student is enrolled in course.
     */
    boolean existsByStudentAndCourse(User student, Course course);

    /**
     * Check if student is enrolled in course by IDs.
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Find all enrollments by student.
     */
    Page<Enrollment> findByStudent(User student, Pageable pageable);

    /**
     * Find all enrollments by student ID.
     */
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Find all enrollments by course.
     */
    Page<Enrollment> findByCourse(Course course, Pageable pageable);

    /**
     * Find all enrollments by course ID.
     */
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Find enrollments with high progress (completed).
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.progress >= 100")
    Page<Enrollment> findCompletedEnrollmentsByStudent(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * Count enrollments by student.
     */
    long countByStudent(User student);

    /**
     * Count enrollments by student ID.
     */
    long countByStudentId(Long studentId);

    /**
     * Count enrollments by course.
     */
    long countByCourse(Course course);

    /**
     * Count enrollments by course ID.
     */
    long countByCourseId(Long courseId);

    /**
     * Count completed enrollments by student (progress >= 100).
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId AND e.progress >= 100")
    long countCompletedEnrollmentsByStudent(@Param("studentId") Long studentId);

    /**
     * Get average progress for a course.
     */
    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.course.id = :courseId")
    Double getAverageProgressByCourse(@Param("courseId") Long courseId);

    /**
     * Get completion rate for a course.
     */
    @Query("SELECT COUNT(e) * 100.0 / (SELECT COUNT(e2) FROM Enrollment e2 WHERE e2.course.id = :courseId) " +
            "FROM Enrollment e WHERE e.course.id = :courseId AND e.progress >= 100")
    Double getCompletionRateByCourse(@Param("courseId") Long courseId);
}