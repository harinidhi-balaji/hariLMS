package com.hari.lms.entity;

import com.hari.lms.enums.ContentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Lesson entity representing lessons within courses in the LMS system.
 * 
 * @author Hari Parthu
 */
@Entity
@Table(name = "lessons")
@EntityListeners(AuditingEntityListener.class)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Sequence number is required")
    @PositiveOrZero(message = "Sequence number must be zero or positive")
    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @NotBlank(message = "Lesson title is required")
    @Size(min = 3, max = 100, message = "Lesson title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotNull(message = "Content type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ContentType contentType;

    @Size(max = 500, message = "Content URL must not exceed 500 characters")
    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Lesson() {
    }

    public Lesson(Course course, Integer sequenceNumber, String title, ContentType contentType) {
        this.course = course;
        this.sequenceNumber = sequenceNumber;
        this.title = title;
        this.contentType = contentType;
    }

    // Convenience methods
    public boolean hasTextContent() {
        return contentType == ContentType.TEXT && contentText != null && !contentText.trim().isEmpty();
    }

    public boolean hasUrlContent() {
        return (contentType == ContentType.VIDEO || contentType == ContentType.PDF) &&
                contentUrl != null && !contentUrl.trim().isEmpty();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
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
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id) &&
                Objects.equals(title, lesson.title) &&
                Objects.equals(sequenceNumber, lesson.sequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, sequenceNumber);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", sequenceNumber=" + sequenceNumber +
                ", title='" + title + '\'' +
                ", contentType=" + contentType +
                ", course=" + (course != null ? course.getTitle() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}