package com.hari.lms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Course entity representing courses in the LMS system.
 * 
 * @author Hari Parthu
 */
@Entity
@Table(name = "courses")
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course title is required")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank(message = "Course description is required")
    @Size(min = 10, max = 1000, message = "Course description must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Course price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Course price must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sequenceNumber ASC")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Course() {
    }

    public Course(String title, String description, BigDecimal price, User instructor) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.instructor = instructor;
        this.isApproved = false;
    }

    // Convenience methods
    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setCourse(this);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
        lesson.setCourse(null);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setCourse(null);
    }

    public int getTotalLessons() {
        return lessons.size();
    }

    public int getTotalEnrollments() {
        return enrollments.size();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(title, course.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", isApproved=" + isApproved +
                ", instructor=" + (instructor != null ? instructor.getUsername() : null) +
                ", totalLessons=" + getTotalLessons() +
                ", totalEnrollments=" + getTotalEnrollments() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}