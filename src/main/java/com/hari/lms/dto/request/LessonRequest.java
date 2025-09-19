package com.hari.lms.dto.request;

import com.hari.lms.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO for lesson creation and update requests.
 * 
 * @author Hari Parthu
 */
public class LessonRequest {

    @NotNull(message = "Order index is required")
    @PositiveOrZero(message = "Order index must be zero or positive")
    private Integer orderIndex;

    @NotBlank(message = "Lesson title is required")
    @Size(min = 3, max = 100, message = "Lesson title must be between 3 and 100 characters")
    private String title;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    @Size(max = 500, message = "Content URL must not exceed 500 characters")
    private String contentUrl;

    private String contentText;

    // Constructors
    public LessonRequest() {
    }

    public LessonRequest(Integer orderIndex, String title, ContentType contentType) {
        this.orderIndex = orderIndex;
        this.title = title;
        this.contentType = contentType;
    }

    // Getters and Setters
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
}