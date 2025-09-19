package com.hari.lms.dto.response;

import com.hari.lms.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO for lesson response data.
 * 
 * @author Hari Parthu
 */
public class LessonResponse {

    private Long id;

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("course_title")
    private String courseTitle;

    @JsonProperty("order_index")
    private Integer orderIndex;

    private String title;

    @JsonProperty("content_type")
    private ContentType contentType;

    @JsonProperty("content_url")
    private String contentUrl;

    @JsonProperty("content_text")
    private String contentText;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public LessonResponse() {
    }

    public LessonResponse(Long id, Long courseId, String courseTitle, Integer orderIndex, String title,
            ContentType contentType, String contentUrl, String contentText,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.orderIndex = orderIndex;
        this.title = title;
        this.contentType = contentType;
        this.contentUrl = contentUrl;
        this.contentText = contentText;
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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
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
}