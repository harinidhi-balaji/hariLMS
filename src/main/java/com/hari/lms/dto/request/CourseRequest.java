package com.hari.lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for course creation and update requests.
 * 
 * @author Hari Parthu
 */
public class CourseRequest {

    @NotBlank(message = "Course title is required")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Course description is required")
    @Size(min = 10, max = 1000, message = "Course description must be between 10 and 1000 characters")
    private String description;

    // Constructors
    public CourseRequest() {
    }

    public CourseRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
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
}