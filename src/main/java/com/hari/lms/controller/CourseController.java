package com.hari.lms.controller;

import com.hari.lms.dto.request.CourseRequest;
import com.hari.lms.dto.response.CourseResponse;
import com.hari.lms.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for course operations.
 * 
 * @author Hari Parthu
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * Create a new course.
     */
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Create Course", description = "Create a new course (Instructor/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid course data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse courseResponse = courseService.createCourse(courseRequest);
        return ResponseEntity.ok(courseResponse);
    }

    /**
     * Update an existing course.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Update Course", description = "Update an existing course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseResponse> updateCourse(
            @Parameter(description = "Course ID") @PathVariable Long id,
            @Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse courseResponse = courseService.updateCourse(id, courseRequest);
        return ResponseEntity.ok(courseResponse);
    }

    /**
     * Get course by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Course", description = "Get course details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseResponse> getCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        CourseResponse courseResponse = courseService.getCourse(id);
        return ResponseEntity.ok(courseResponse);
    }

    /**
     * Get all courses with pagination.
     */
    @GetMapping
    @Operation(summary = "Get All Courses", description = "Get all courses with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    })
    public ResponseEntity<Page<CourseResponse>> getAllCourses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<CourseResponse> courses = courseService.getAllCourses(page, size, sortBy, sortDir);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get published courses with pagination.
     */
    @GetMapping("/published")
    @Operation(summary = "Get Published Courses", description = "Get all published courses with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Published courses retrieved successfully")
    })
    public ResponseEntity<Page<CourseResponse>> getPublishedCourses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<CourseResponse> courses = courseService.getPublishedCourses(page, size, sortBy, sortDir);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get current user's courses.
     */
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Get My Courses", description = "Get courses created by current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User courses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseResponse>> getMyCourses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<CourseResponse> courses = courseService.getMyCourses(page, size, sortBy, sortDir);
        return ResponseEntity.ok(courses);
    }

    /**
     * Search courses.
     */
    @GetMapping("/search")
    @Operation(summary = "Search Courses", description = "Search courses by title or description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    public ResponseEntity<Page<CourseResponse>> searchCourses(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<CourseResponse> courses = courseService.searchCourses(q, page, size, sortBy, sortDir);
        return ResponseEntity.ok(courses);
    }

    /**
     * Submit course for approval.
     */
    @PutMapping("/{id}/submit")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Submit Course", description = "Submit course for admin approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course submitted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseResponse> submitCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        CourseResponse courseResponse = courseService.submitForApproval(id);
        return ResponseEntity.ok(courseResponse);
    }

    /**
     * Approve course (Admin only).
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve Course", description = "Approve a pending course (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course approved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseResponse> approveCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        CourseResponse courseResponse = courseService.approveCourse(id);
        return ResponseEntity.ok(courseResponse);
    }

    /**
     * Reject course (Admin only).
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject Course", description = "Reject a pending course (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseResponse> rejectCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        CourseResponse courseResponse = courseService.rejectCourse(id);
        return ResponseEntity.ok(courseResponse);
    }

    /**
     * Get pending courses (Admin only).
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Pending Courses", description = "Get courses pending approval (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending courses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseResponse>> getPendingCourses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<CourseResponse> courses = courseService.getPendingCourses(page, size, sortBy, sortDir);
        return ResponseEntity.ok(courses);
    }

    /**
     * Delete course.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Delete Course", description = "Delete a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }
}