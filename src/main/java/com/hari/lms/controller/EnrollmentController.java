package com.hari.lms.controller;

import com.hari.lms.dto.response.EnrollmentResponse;
import com.hari.lms.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for enrollment operations.
 * 
 * @author Hari Parthu
 */
@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Course enrollment APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * Enroll in a course.
     */
    @PostMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enroll in Course", description = "Enroll current student in a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully enrolled in course"),
            @ApiResponse(responseCode = "400", description = "Invalid enrollment request"),
            @ApiResponse(responseCode = "403", description = "Access denied - Only students can enroll"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "Already enrolled in this course")
    })
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        EnrollmentResponse enrollment = enrollmentService.enrollInCourse(courseId);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * Check enrollment status.
     */
    @GetMapping("/courses/{courseId}/status")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Check Enrollment Status", description = "Check if current student is enrolled in a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment status retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Boolean> checkEnrollmentStatus(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        boolean isEnrolled = enrollmentService.isEnrolledInCourse(courseId);
        return ResponseEntity.ok(isEnrolled);
    }

    /**
     * Get my enrollments.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get My Enrollments", description = "Get current student's enrollments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Only students have enrollments")
    })
    public ResponseEntity<Page<EnrollmentResponse>> getMyEnrollments(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "enrollmentDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        Page<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments(page, size, sortBy, sortDir);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get course enrollments (Admin/Instructor only).
     */
    @GetMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Get Course Enrollments", description = "Get enrollments for a specific course (Admin/Instructor only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course enrollments retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Page<EnrollmentResponse>> getCourseEnrollments(
            @Parameter(description = "Course ID") @PathVariable Long courseId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "enrollmentDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        Page<EnrollmentResponse> enrollments = enrollmentService.getCourseEnrollments(courseId, page, size, sortBy,
                sortDir);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Unenroll from course.
     */
    @DeleteMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Unenroll from Course", description = "Unenroll current student from a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully unenrolled from course"),
            @ApiResponse(responseCode = "400", description = "Not enrolled in this course"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> unenrollFromCourse(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        enrollmentService.unenrollFromCourse(courseId);
        return ResponseEntity.ok().build();
    }

    /**
     * Update enrollment progress.
     */
    @PutMapping("/courses/{courseId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update Progress", description = "Update enrollment progress for a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid progress value or not enrolled"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @Parameter(description = "Course ID") @PathVariable Long courseId,
            @Parameter(description = "Progress percentage (0-100)") @RequestParam Integer progress) {
        EnrollmentResponse enrollment = enrollmentService.updateProgress(courseId, progress);
        return ResponseEntity.ok(enrollment);
    }
}