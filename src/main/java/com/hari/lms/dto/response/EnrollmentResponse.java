package com.hari.lms.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO for enrollment response data.
 * 
 * @author Hari Parthu
 */
public class EnrollmentResponse {

    private Long id;

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("course_title")
    private String courseTitle;

    @JsonProperty("student_id")
    private Long studentId;

    @JsonProperty("student_username")
    private String studentUsername;

    @JsonProperty("student_full_name")
    private String studentFullName;

    @JsonProperty("enrollment_date")
    private LocalDateTime enrollmentDate;

    private Integer progress;

    @JsonProperty("completed_lessons")
    private String completedLessons;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public EnrollmentResponse() {
    }

    public EnrollmentResponse(Long id, Long courseId, String courseTitle, Long studentId,
            String studentUsername, String studentFullName, LocalDateTime enrollmentDate,
            Integer progress, String completedLessons, LocalDateTime updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.studentId = studentId;
        this.studentUsername = studentUsername;
        this.studentFullName = studentFullName;
        this.enrollmentDate = enrollmentDate;
        this.progress = progress;
        this.completedLessons = completedLessons;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
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

    // Utility methods
    public boolean isCompleted() {
        return progress != null && progress >= 100;
    }

    public String getProgressPercentage() {
        return (progress != null ? progress : 0) + "%";
    }
}