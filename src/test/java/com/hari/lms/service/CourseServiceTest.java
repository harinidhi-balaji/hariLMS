package com.hari.lms.service;

import com.hari.lms.dto.request.CourseRequest;
import com.hari.lms.dto.response.CourseResponse;
import com.hari.lms.entity.Course;
import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import com.hari.lms.exception.ResourceNotFoundException;
import com.hari.lms.exception.UnauthorizedAccessException;
import com.hari.lms.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseService.
 * 
 * @author Hari Parthu
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private User admin;
    private User student;
    private Course course;
    private CourseRequest courseRequest;
    private CourseResponse courseResponse;

    @BeforeEach
    void setUp() {
        // Setup test users
        instructor = new User("instructor1", "instructor1@test.com", "Instructor One", "password", Role.INSTRUCTOR);
        instructor.setId(1L);
        instructor.setCreatedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        admin = new User("admin", "admin@test.com", "Admin User", "password", Role.ADMIN);
        admin.setId(2L);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        student = new User("student1", "student1@test.com", "Student One", "password", Role.STUDENT);
        student.setId(3L);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        // Setup test course
        course = new Course("Test Course", "Test Description", new BigDecimal("49.99"), instructor);
        course.setId(1L);
        course.setIsApproved(false);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        // Setup test DTOs
        courseRequest = new CourseRequest("Test Course", "Test Description");

        courseResponse = new CourseResponse();
        courseResponse.setId(1L);
        courseResponse.setTitle("Test Course");
        courseResponse.setDescription("Test Description");
        courseResponse.setIsApproved(false);
        courseResponse.setInstructorUsername("instructor1");
        courseResponse.setInstructorEmail("instructor1@test.com");
        courseResponse.setTotalLessons(0);
        courseResponse.setTotalEnrollments(0);
        courseResponse.setCreatedAt(LocalDateTime.now());
        courseResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createCourse_WhenInstructorUser_ShouldCreateCourse() {
        // Given
        when(authService.getCurrentUserEntity()).thenReturn(instructor);
        when(authService.isInstructor()).thenReturn(true);
        when(authService.isAdmin()).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(modelMapper.map(course, CourseResponse.class)).thenReturn(courseResponse);

        // When
        CourseResponse result = courseService.createCourse(courseRequest);

        // Then
        assertNotNull(result);
        assertEquals("Test Course", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(false, result.getIsApproved());
        assertEquals("instructor1", result.getInstructorUsername());

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_WhenStudentUser_ShouldThrowUnauthorizedAccessException() {
        // Given
        when(authService.getCurrentUserEntity()).thenReturn(student);
        when(authService.isInstructor()).thenReturn(false);
        when(authService.isAdmin()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedAccessException.class, () -> courseService.createCourse(courseRequest));

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void getCourse_WhenCourseExists_ShouldReturnCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(modelMapper.map(course, CourseResponse.class)).thenReturn(courseResponse);

        // When
        CourseResponse result = courseService.getCourse(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Course", result.getTitle());
    }

    @Test
    void getCourse_WhenCourseNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourse(1L));
    }

    @Test
    void updateCourse_WhenAuthorizedUser_ShouldUpdateCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(authService.getCurrentUserEntity()).thenReturn(instructor);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(modelMapper.map(course, CourseResponse.class)).thenReturn(courseResponse);

        CourseRequest updateRequest = new CourseRequest("Updated Course", "Updated Description");

        // When
        CourseResponse result = courseService.updateCourse(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void getAllCourses_ShouldReturnPagedCourses() {
        // Given
        List<Course> courses = Arrays.asList(course);
        Page<Course> coursePage = new PageImpl<>(courses);

        when(courseRepository.findAll(any(Pageable.class))).thenReturn(coursePage);
        when(modelMapper.map(course, CourseResponse.class)).thenReturn(courseResponse);

        // When
        Page<CourseResponse> result = courseService.getAllCourses(0, 10, "createdAt", "desc");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Course", result.getContent().get(0).getTitle());
    }

    @Test
    void submitForApproval_WhenDraftCourse_ShouldChangeToPending() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(authService.getCurrentUserEntity()).thenReturn(instructor);

        Course pendingCourse = new Course(course.getTitle(), course.getDescription(), course.getPrice(),
                course.getInstructor());
        pendingCourse.setId(course.getId());
        pendingCourse.setIsApproved(false);

        when(courseRepository.save(any(Course.class))).thenReturn(pendingCourse);

        CourseResponse pendingResponse = new CourseResponse();
        pendingResponse.setIsApproved(false);
        when(modelMapper.map(any(Course.class), eq(CourseResponse.class))).thenReturn(pendingResponse);

        // When
        CourseResponse result = courseService.submitForApproval(1L);

        // Then
        assertEquals(false, result.getIsApproved());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void approveCourse_WhenAdminUser_ShouldApprove() {
        // Given
        course.setIsApproved(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(authService.isAdmin()).thenReturn(true);

        Course publishedCourse = new Course(course.getTitle(), course.getDescription(), course.getPrice(),
                course.getInstructor());
        publishedCourse.setId(course.getId());
        publishedCourse.setIsApproved(true);

        when(courseRepository.save(any(Course.class))).thenReturn(publishedCourse);

        CourseResponse publishedResponse = new CourseResponse();
        publishedResponse.setIsApproved(true);
        when(modelMapper.map(any(Course.class), eq(CourseResponse.class))).thenReturn(publishedResponse);

        // When
        CourseResponse result = courseService.approveCourse(1L);

        // Then
        assertEquals(true, result.getIsApproved());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void approveCourse_WhenNonAdminUser_ShouldThrowUnauthorizedAccessException() {
        // Given
        when(authService.isAdmin()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedAccessException.class, () -> courseService.approveCourse(1L));

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteCourse_WhenAuthorizedAndDraft_ShouldDeleteCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(authService.getCurrentUserEntity()).thenReturn(instructor);

        // When
        courseService.deleteCourse(1L);

        // Then
        verify(courseRepository).delete(course);
    }
}