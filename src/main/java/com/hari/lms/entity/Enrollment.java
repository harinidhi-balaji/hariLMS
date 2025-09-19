package com.hari.lms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Enrollment entity representing student enrollments in courses.
 * 
 * @author Hari Parthu
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(name = "unique_enrollment", columnNames = { "student_id", "course_id" })
})
@EntityListeners(AuditingEntityListener.class)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false)
    @CreatedDate
    private LocalDateTime enrollmentDate;

    @NotNull(message = "Progress is required")
    @Min(value = 0, message = "Progress must be at least 0")
    @Max(value = 100, message = "Progress cannot exceed 100")
    @Column(nullable = false)
    private Integer progress = 0;

    @Column(name = "completed_lessons", columnDefinition = "TEXT")
    private String completedLessons = "[]"; // JSON array of completed lesson IDs

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", unique = true)
    private Payment payment;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Enrollment() {
    }

    public Enrollment(User student, Course course) {
        this.student = student;
        this.course = course;
        this.progress = 0;
        this.completedLessons = "[]"; // Empty JSON array
    }

    public Enrollment(User student, Course course, Payment payment) {
        this.student = student;
        this.course = course;
        this.payment = payment;
        this.progress = 0;
        this.completedLessons = "[]"; // Empty JSON array
    }

    // Convenience methods
    public boolean isCompleted() {
        return progress >= 100;
    }

    public String getProgressPercentage() {
        return progress + "%";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(String completedLessons) {
        this.completedLessons = completedLessons;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(student, that.student) &&
                Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, course);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", studentId=" + (student != null ? student.getId() : null) +
                ", courseId=" + (course != null ? course.getId() : null) +
                ", progress=" + progress +
                ", completedLessons='" + completedLessons + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", updatedAt=" + updatedAt +
                '}';
    }
}