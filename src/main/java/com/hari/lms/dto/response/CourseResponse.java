package com.hari.lms.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for course response data.
 * 
 * @author Hari Parthu
 */
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;

    @JsonProperty("is_approved")
    private Boolean isApproved;

    @JsonProperty("instructor_username")
    private String instructorUsername;

    @JsonProperty("instructor_email")
    private String instructorEmail;

    @JsonProperty("total_lessons")
    private int totalLessons;

    @JsonProperty("total_enrollments")
    private int totalEnrollments;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public CourseResponse() {
    }

    public CourseResponse(Long id, String title, String description, BigDecimal price, Boolean isApproved,
            String instructorUsername, String instructorEmail, int totalLessons,
            int totalEnrollments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.isApproved = isApproved;
        this.instructorUsername = instructorUsername;
        this.instructorEmail = instructorEmail;
        this.totalLessons = totalLessons;
        this.totalEnrollments = totalEnrollments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public int getTotalEnrollments() {
        return totalEnrollments;
    }

    public void setTotalEnrollments(int totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
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
}