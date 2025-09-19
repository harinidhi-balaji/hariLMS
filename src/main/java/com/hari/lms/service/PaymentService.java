package com.hari.lms.service;

import com.hari.lms.entity.Course;
import com.hari.lms.entity.Enrollment;
import com.hari.lms.entity.Payment;
import com.hari.lms.entity.User;
import com.hari.lms.enums.PaymentStatus;
import com.hari.lms.repository.CourseRepository;
import com.hari.lms.repository.EnrollmentRepository;
import com.hari.lms.repository.PaymentRepository;
import com.hari.lms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for payment operations.
 * Handles payment processing, validation, and enrollment creation after
 * successful payment.
 * 
 * @author Hari Parthu
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * Create a payment for course enrollment.
     */
    public Payment createPayment(Long studentId, Long courseId) {
        logger.info("Creating payment for student {} and course {}", studentId, courseId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if student already has a successful payment for this course
        Optional<Payment> existingPayment = paymentRepository
                .findCompletedPaymentByStudentAndCourse(studentId, courseId);

        if (existingPayment.isPresent()) {
            throw new RuntimeException("Student has already paid for this course");
        }

        // Check if student is already enrolled (in case of free courses)
        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
        if (alreadyEnrolled) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setCourse(course);
        payment.setAmount(course.getPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Process payment (simulate payment gateway).
     */
    public Payment processPayment(Long paymentId) {
        logger.info("Processing payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in pending status");
        }

        try {
            // Update payment status to processing
            payment.setStatus(PaymentStatus.PROCESSING);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Simulate payment processing delay
            Thread.sleep(2000);

            // Simulate payment gateway response (90% success rate for demo)
            boolean paymentSuccessful = Math.random() > 0.1;

            if (paymentSuccessful) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(LocalDateTime.now());

                // Create enrollment after successful payment
                createEnrollmentFromPayment(payment);

                logger.info("Payment {} completed successfully", paymentId);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                logger.warn("Payment {} failed", paymentId);
            }

        } catch (Exception e) {
            logger.error("Error processing payment {}: {}", paymentId, e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    /**
     * Create enrollment after successful payment.
     */
    private void createEnrollmentFromPayment(Payment payment) {
        logger.info("Creating enrollment from payment {}", payment.getId());

        Enrollment enrollment = new Enrollment(
                payment.getStudent(),
                payment.getCourse(),
                payment);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Update payment with enrollment reference
        payment.setEnrollment(savedEnrollment);
        paymentRepository.save(payment);

        logger.info("Enrollment created successfully for payment {}", payment.getId());
    }

    /**
     * Get payment by ID.
     */
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    /**
     * Get payment by transaction ID.
     */
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    /**
     * Get payments for a student.
     */
    public Page<Payment> getPaymentsByStudent(User student, Pageable pageable) {
        return paymentRepository.findByStudent(student, pageable);
    }

    /**
     * Get payments by course.
     */
    public Page<Payment> getPaymentsByCourse(Long courseId, Pageable pageable) {
        return paymentRepository.findByCourseId(courseId, pageable);
    }

    /**
     * Check if student has paid for a course.
     */
    public boolean hasStudentPaidForCourse(Long studentId, Long courseId) {
        return paymentRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, courseId, PaymentStatus.COMPLETED);
    }

    /**
     * Get payment statistics.
     */
    public PaymentStats getPaymentStats() {
        PaymentStats stats = new PaymentStats();
        stats.setPendingPayments(paymentRepository.countByStatus(PaymentStatus.PENDING));
        stats.setCompletedPayments(paymentRepository.countByStatus(PaymentStatus.COMPLETED));
        stats.setFailedPayments(paymentRepository.countByStatus(PaymentStatus.FAILED));
        return stats;
    }

    /**
     * Get recent payments for admin.
     */
    public Page<Payment> getRecentPayments(Pageable pageable) {
        return paymentRepository.findRecentPayments(pageable);
    }

    /**
     * Cancel a pending payment.
     */
    public Payment cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Generate unique transaction ID.
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Inner class for payment statistics.
     */
    public static class PaymentStats {
        private long pendingPayments;
        private long completedPayments;
        private long failedPayments;

        // Getters and setters
        public long getPendingPayments() {
            return pendingPayments;
        }

        public void setPendingPayments(long pendingPayments) {
            this.pendingPayments = pendingPayments;
        }

        public long getCompletedPayments() {
            return completedPayments;
        }

        public void setCompletedPayments(long completedPayments) {
            this.completedPayments = completedPayments;
        }

        public long getFailedPayments() {
            return failedPayments;
        }

        public void setFailedPayments(long failedPayments) {
            this.failedPayments = failedPayments;
        }
    }
}