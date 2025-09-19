package com.hari.lms.service;

import com.hari.lms.dto.response.EnrollmentResponse;
import com.hari.lms.entity.Course;
import com.hari.lms.entity.Enrollment;
import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import com.hari.lms.exception.ResourceNotFoundException;
import com.hari.lms.exception.UnauthorizedAccessException;
import com.hari.lms.repository.CourseRepository;
import com.hari.lms.repository.EnrollmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for enrollment operations.
 * 
 * @author Hari Parthu
 */
@Service
@Transactional
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Enroll a student in a course.
     */
    public EnrollmentResponse enrollInCourse(Long courseId) {
        User currentUser = authService.getCurrentUserEntity();

        // Only students can enroll in courses
        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedAccessException("Only students can enroll in courses");
        }

        // Find the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Check if course is approved
        if (!course.getIsApproved()) {
            throw new RuntimeException("Cannot enroll in unapproved courses");
        }

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentAndCourse(currentUser, course)) {
            throw new RuntimeException("You are already enrolled in this course");
        }

        // Create enrollment
        Enrollment enrollment = new Enrollment(currentUser, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return mapToEnrollmentResponse(savedEnrollment);
    }

    /**
     * Check if current user is enrolled in a course.
     */
    @Transactional(readOnly = true)
    public boolean isEnrolledInCourse(Long courseId) {
        User currentUser = authService.getCurrentUserEntity();
        return enrollmentRepository.existsByStudentIdAndCourseId(currentUser.getId(), courseId);
    }

    /**
     * Get current user's enrollments.
     */
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getMyEnrollments(int page, int size, String sortBy, String sortDir) {
        User currentUser = authService.getCurrentUserEntity();

        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedAccessException("Only students have enrollments");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Enrollment> enrollments = enrollmentRepository.findByStudent(currentUser, pageable);
        return enrollments.map(this::mapToEnrollmentResponse);
    }

    /**
     * Get enrollments for a specific course (Admin/Instructor only).
     */
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getCourseEnrollments(Long courseId, int page, int size, String sortBy,
            String sortDir) {
        User currentUser = authService.getCurrentUserEntity();

        // Find the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Check authorization
        if (currentUser.getRole() == Role.STUDENT) {
            throw new UnauthorizedAccessException("Students cannot view course enrollments");
        }

        // Instructors can only view enrollments for their own courses
        if (currentUser.getRole() == Role.INSTRUCTOR && !course.getInstructor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only view enrollments for your own courses");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Enrollment> enrollments = enrollmentRepository.findByCourse(course, pageable);
        return enrollments.map(this::mapToEnrollmentResponse);
    }

    /**
     * Unenroll from a course.
     */
    public void unenrollFromCourse(Long courseId) {
        User currentUser = authService.getCurrentUserEntity();

        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedAccessException("Only students can unenroll from courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(currentUser, course)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        enrollmentRepository.delete(enrollment);
    }

    /**
     * Update enrollment progress.
     */
    public EnrollmentResponse updateProgress(Long courseId, Integer progress) {
        User currentUser = authService.getCurrentUserEntity();

        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedAccessException("Only students can update enrollment progress");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(currentUser, course)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        if (progress < 0 || progress > 100) {
            throw new RuntimeException("Progress must be between 0 and 100");
        }

        enrollment.setProgress(progress);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return mapToEnrollmentResponse(savedEnrollment);
    }

    // Helper methods
    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        EnrollmentResponse response = modelMapper.map(enrollment, EnrollmentResponse.class);
        response.setCourseTitle(enrollment.getCourse().getTitle());
        response.setStudentUsername(enrollment.getStudent().getUsername());
        response.setStudentFullName(enrollment.getStudent().getFullName());
        return response;
    }
}