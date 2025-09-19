package com.hari.lms.repository;

import com.hari.lms.entity.Payment;
import com.hari.lms.entity.User;
import com.hari.lms.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations.
 * 
 * @author Hari Parthu
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by transaction ID.
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find payments by student.
     */
    Page<Payment> findByStudent(User student, Pageable pageable);

    /**
     * Find payments by student and status.
     */
    List<Payment> findByStudentAndStatus(User student, PaymentStatus status);

    /**
     * Find payments by course ID.
     */
    @Query("SELECT p FROM Payment p WHERE p.course.id = :courseId")
    Page<Payment> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * Find payments by status.
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Check if payment exists for student and course.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p WHERE p.student.id = :studentId AND p.course.id = :courseId AND p.status = :status")
    boolean existsByStudentIdAndCourseIdAndStatus(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
            @Param("status") PaymentStatus status);

    /**
     * Find successful payment for student and course.
     */
    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.course.id = :courseId AND p.status = 'COMPLETED'")
    Optional<Payment> findCompletedPaymentByStudentAndCourse(@Param("studentId") Long studentId,
            @Param("courseId") Long courseId);

    /**
     * Count payments by status.
     */
    long countByStatus(PaymentStatus status);

    /**
     * Find recent payments for admin dashboard.
     */
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    Page<Payment> findRecentPayments(Pageable pageable);
}