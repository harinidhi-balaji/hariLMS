package com.hari.lms.service;

import com.hari.lms.dto.request.CourseRequest;
import com.hari.lms.dto.response.CourseResponse;
import com.hari.lms.entity.Course;
import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import com.hari.lms.exception.ResourceNotFoundException;
import com.hari.lms.exception.UnauthorizedAccessException;
import com.hari.lms.repository.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for course operations.
 * 
 * @author Hari Parthu
 */
@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Create a new course.
     */
    public CourseResponse createCourse(CourseRequest courseRequest) {
        User currentUser = authService.getCurrentUserEntity();

        // Only instructors and admins can create courses
        if (!authService.isInstructor() && !authService.isAdmin()) {
            throw new UnauthorizedAccessException("Only instructors and admins can create courses");
        }

        Course course = new Course();
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setInstructor(currentUser);
        course.setIsApproved(false); // Draft status = false

        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    /**
     * Update an existing course.
     */
    public CourseResponse updateCourse(Long courseId, CourseRequest courseRequest) {
        Course course = getCourseById(courseId);
        User currentUser = authService.getCurrentUserEntity();

        // Check authorization
        if (!canEditCourse(course, currentUser)) {
            throw new UnauthorizedAccessException("You are not authorized to edit this course");
        }

        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());

        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    /**
     * Get course by ID.
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long courseId) {
        Course course = getCourseById(courseId);
        return mapToCourseResponse(course);
    }

    /**
     * Get all courses with pagination.
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllCourses(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> courses = courseRepository.findAll(pageable);

        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Get published courses with pagination.
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> getPublishedCourses(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> courses = courseRepository.findByIsApproved(true, pageable);

        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Get courses by instructor.
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCoursesByInstructor(Long instructorId, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> courses = courseRepository.findByInstructorId(instructorId, pageable);

        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Get current user's courses.
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> getMyCourses(int page, int size, String sortBy, String sortDir) {
        User currentUser = authService.getCurrentUserEntity();
        return getCoursesByInstructor(currentUser.getId(), page, size, sortBy, sortDir);
    }

    /**
     * Search courses by title or description.
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchCourses(String searchTerm, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> courses = courseRepository.searchByTitleOrDescription(searchTerm, pageable);

        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Submit course for approval.
     */
    public CourseResponse submitForApproval(Long courseId) {
        Course course = getCourseById(courseId);
        User currentUser = authService.getCurrentUserEntity();

        // Check authorization
        if (!canEditCourse(course, currentUser)) {
            throw new UnauthorizedAccessException("You are not authorized to submit this course");
        }

        // Only non-approved courses can be submitted
        if (course.getIsApproved() == true) {
            throw new RuntimeException("Approved courses cannot be resubmitted");
        }

        // Course remains unapproved but is now pending review
        // In a more complex system, you might have a separate 'pending' field
        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    /**
     * Approve course (Admin only).
     */
    public CourseResponse approveCourse(Long courseId) {
        if (!authService.isAdmin()) {
            throw new UnauthorizedAccessException("Only administrators can approve courses");
        }

        Course course = getCourseById(courseId);

        // Only unapproved courses can be approved
        if (course.getIsApproved() == true) {
            throw new RuntimeException("Course is already approved");
        }

        course.setIsApproved(true);
        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    /**
     * Reject course (Admin only).
     */
    public CourseResponse rejectCourse(Long courseId) {
        if (!authService.isAdmin()) {
            throw new UnauthorizedAccessException("Only administrators can reject courses");
        }

        Course course = getCourseById(courseId);

        // Only approved courses can be rejected (set back to unapproved)
        if (course.getIsApproved() != true) {
            throw new RuntimeException("Only approved courses can be rejected");
        }

        course.setIsApproved(false);
        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    /**
     * Delete course.
     */
    public void deleteCourse(Long courseId) {
        Course course = getCourseById(courseId);
        User currentUser = authService.getCurrentUserEntity();

        // Check authorization
        if (!canDeleteCourse(course, currentUser)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this course");
        }

        courseRepository.delete(course);
    }

    /**
     * Get pending courses for approval (Admin only).
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> getPendingCourses(int page, int size, String sortBy, String sortDir) {
        if (!authService.isAdmin()) {
            throw new UnauthorizedAccessException("Only administrators can view pending courses");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> courses = courseRepository.findByIsApproved(false, pageable);

        return courses.map(this::mapToCourseResponse);
    }

    // Helper methods

    private Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    private boolean canEditCourse(Course course, User user) {
        return user.getRole() == Role.ADMIN || course.getInstructor().getId().equals(user.getId());
    }

    private boolean canDeleteCourse(Course course, User user) {
        return user.getRole() == Role.ADMIN ||
                (course.getInstructor().getId().equals(user.getId()) &&
                        course.getIsApproved() == false);
    }

    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse response = modelMapper.map(course, CourseResponse.class);
        response.setPrice(course.getPrice()); // Explicitly set the price
        response.setInstructorUsername(course.getInstructor().getUsername());
        response.setInstructorEmail(course.getInstructor().getEmail());
        response.setTotalLessons(course.getTotalLessons());
        response.setTotalEnrollments(course.getTotalEnrollments());
        return response;
    }
}