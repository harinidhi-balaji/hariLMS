package com.hari.lms.controller;

import com.hari.lms.dto.response.CourseResponse;
import com.hari.lms.entity.Course;
import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import com.hari.lms.repository.UserRepository;
import com.hari.lms.repository.CourseRepository;
import com.hari.lms.repository.EnrollmentRepository;
import com.hari.lms.service.AuthService;
import com.hari.lms.service.CourseService;
import com.hari.lms.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Web controller for serving Thymeleaf templates.
 * 
 * @author Hari Parthu
 */
@Controller
public class WebController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * Home page.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Welcome");

        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }

        return "index";
    }

    /**
     * Home page (alternative route).
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Home");
        return "index";
    }

    /**
     * Login page.
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success,
            Model model, HttpServletResponse response) {

        // Prevent caching of login page to ensure fresh CSRF tokens
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }

        if (logout != null) {
            model.addAttribute("message", "You've been logged out successfully.");
        }

        if (success != null) {
            model.addAttribute("message", success);
        }

        model.addAttribute("title", "Sign In");
        return "auth/login";
    }

    /**
     * Signup page.
     */
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Sign Up");
        return "auth/signup";
    }

    /**
     * Handle signup form submission.
     */
    @PostMapping("/signup")
    public String processSignup(@RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model) {
        try {
            // Create signup request
            com.hari.lms.dto.request.SignupRequest signupRequest = new com.hari.lms.dto.request.SignupRequest();
            signupRequest.setFullName(fullName);
            signupRequest.setUsername(username);
            signupRequest.setEmail(email);
            signupRequest.setPassword(password);
            signupRequest.setRole(com.hari.lms.enums.Role.valueOf(role));

            // Register the user
            authService.registerUser(signupRequest);

            // Redirect to login with success message
            return "redirect:/login?success=Account created successfully! Please log in.";
        } catch (RuntimeException e) {
            // Handle registration errors
            model.addAttribute("error", e.getMessage());
            model.addAttribute("title", "Sign Up");
            return "auth/signup";
        }
    }

    /**
     * Dashboard page (role-based redirect).
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login?error=true";
        }

        try {
            // Debug logging
            System.out.println("Dashboard accessed by user: " + auth.getName());
            System.out.println("User authorities: " + auth.getAuthorities());

            // Redirect based on role
            if (authService.isAdmin()) {
                System.out.println("Redirecting to admin dashboard");
                return "redirect:/admin";
            } else if (authService.isInstructor()) {
                System.out.println("Redirecting to instructor dashboard");
                return "redirect:/instructor";
            } else if (authService.isStudent()) {
                System.out.println("Redirecting to student dashboard");
                return "redirect:/student";
            }

            // Default fallback
            model.addAttribute("title", "Dashboard");
            model.addAttribute("username", auth.getName());
            return "dashboard";
        } catch (Exception e) {
            System.err.println("Error in dashboard method: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/login?error=true";
        }
    }

    /**
     * Courses page.
     */
    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("title", "Courses");

        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        } else {
            model.addAttribute("username", null);
        }

        // Load approved courses
        var courses = courseService.getPublishedCourses(0, 50, "createdAt", "desc");
        model.addAttribute("courses", courses.getContent());

        // If user is authenticated and is a student, check enrollment status for each
        // course
        if (isAuthenticated) {
            try {
                User currentUser = authService.getCurrentUserEntity();
                if (currentUser.getRole() == Role.STUDENT) {
                    Map<Long, Boolean> enrollmentStatus = new HashMap<>();
                    for (CourseResponse course : courses.getContent()) {
                        enrollmentStatus.put(course.getId(), enrollmentService.isEnrolledInCourse(course.getId()));
                    }
                    model.addAttribute("enrollmentStatus", enrollmentStatus);
                }
            } catch (Exception e) {
                // If there's an error getting user info, just continue without enrollment
                // status
                model.addAttribute("enrollmentStatus", new HashMap<>());
            }
        } else {
            model.addAttribute("enrollmentStatus", new HashMap<>());
        }

        return "courses/index";
    }

    /**
     * Course detail page.
     */
    @GetMapping("/courses/{id}")
    public String courseDetail(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("title", "Course Details");
        model.addAttribute("courseId", id);

        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }

        return "courses/detail";
    }

    /**
     * Admin dashboard.
     */
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        // Get statistics for dashboard
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        model.addAttribute("title", "Admin Dashboard");
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalEnrollments", totalEnrollments);
        return "admin/dashboard";
    }

    /**
     * Admin courses page - view all courses.
     */
    @GetMapping("/admin/courses")
    public String adminCourses(Model model) {
        Page<CourseResponse> courses = courseService.getAllCourses(0, 50, "createdAt", "desc");
        model.addAttribute("title", "All Courses");
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("totalCourses", courses.getTotalElements());
        return "admin/courses";
    }

    /**
     * Admin user management page.
     */
    @GetMapping("/admin/users")
    public String adminUsers(Model model, HttpServletRequest request) {
        List<User> users = userRepository.findAll();
        model.addAttribute("title", "User Management");
        model.addAttribute("users", users);

        // Add CSRF token to model
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "admin/users";
    }

    /**
     * Enable a user.
     */
    @PostMapping("/admin/users/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            user.setEnabled(true);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User enabled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to enable user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Disable a user.
     */
    @PostMapping("/admin/users/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            user.setEnabled(false);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User disabled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to disable user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Delete a user.
     */
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            // Don't allow deleting admin users
            if (user.getRole() == com.hari.lms.enums.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete admin users!");
                return "redirect:/admin/users";
            }

            // Check for dependencies before deletion
            boolean canDelete = true;
            StringBuilder errorMessage = new StringBuilder();

            // Check if user is an instructor with courses
            if (user.getRole() == com.hari.lms.enums.Role.INSTRUCTOR) {
                long courseCount = courseRepository.countByInstructor(user);
                if (courseCount > 0) {
                    canDelete = false;
                    errorMessage.append("Cannot delete instructor with ").append(courseCount)
                            .append(" assigned course(s). ");
                }
            }

            // Check if user is a student with enrollments
            if (user.getRole() == com.hari.lms.enums.Role.STUDENT) {
                long enrollmentCount = enrollmentRepository.countByStudent(user);
                if (enrollmentCount > 0) {
                    canDelete = false;
                    errorMessage.append("Cannot delete student with ").append(enrollmentCount)
                            .append(" enrollment(s). ");
                }
            }

            if (!canDelete) {
                errorMessage.append("Please remove all associated records first or disable the user instead.");
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage.toString());
                return "redirect:/admin/users";
            }

            // Safe to delete - no dependencies
            userRepository.delete(user);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Show edit user form.
     */
    @GetMapping("/admin/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("title", "Edit User");
        model.addAttribute("user", user);
        model.addAttribute("roles", com.hari.lms.enums.Role.values());
        return "admin/edit-user";
    }

    /**
     * Update user.
     */
    @PostMapping("/admin/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam(required = false) String password,
            @RequestParam boolean enabled,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            user.setFullName(fullName);
            user.setEmail(email);
            user.setRole(com.hari.lms.enums.Role.valueOf(role));
            user.setEnabled(enabled);

            // Only update password if provided
            if (password != null && !password.trim().isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                user.setPassword(encoder.encode(password));
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ================================
    // Course Management Endpoints
    // ================================

    /**
     * Show add course form.
     */
    @GetMapping("/admin/courses/add")
    public String addCourseForm(Model model) {
        Pageable pageable = PageRequest.of(0, 100); // Get up to 100 instructors
        Page<User> instructorsPage = userRepository.findByRole(com.hari.lms.enums.Role.INSTRUCTOR, pageable);
        model.addAttribute("title", "Add New Course");
        model.addAttribute("instructors", instructorsPage.getContent());
        return "admin/add-course";
    }

    /**
     * Create a new course.
     */
    @PostMapping("/admin/courses/add")
    public String addCourse(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long instructorId,
            @RequestParam(defaultValue = "false") Boolean isApproved,
            RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (title == null || title.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Course title is required.");
                return "redirect:/admin/courses/add";
            }
            if (description == null || description.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Course description is required.");
                return "redirect:/admin/courses/add";
            }
            if (title.length() < 3 || title.length() > 100) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Course title must be between 3 and 100 characters.");
                return "redirect:/admin/courses/add";
            }
            if (description.length() < 10 || description.length() > 1000) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Course description must be between 10 and 1000 characters.");
                return "redirect:/admin/courses/add";
            }

            User instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));

            Course course = new Course();
            course.setTitle(title.trim());
            course.setDescription(description.trim());
            course.setInstructor(instructor);
            course.setIsApproved(isApproved != null ? isApproved : false);

            courseRepository.save(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /**
     * Show edit course form.
     */
    @GetMapping("/admin/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            Pageable pageable = PageRequest.of(0, 100); // Get up to 100 instructors
            Page<User> instructorsPage = userRepository.findByRole(com.hari.lms.enums.Role.INSTRUCTOR, pageable);

            model.addAttribute("title", "Edit Course");
            model.addAttribute("course", course);
            model.addAttribute("instructors", instructorsPage.getContent());
            return "admin/edit-course";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found: " + e.getMessage());
            return "redirect:/admin/courses";
        }
    }

    /**
     * Update an existing course.
     */
    @PostMapping("/admin/courses/{id}/update")
    public String updateCourse(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long instructorId,
            @RequestParam(defaultValue = "false") Boolean isApproved,
            RedirectAttributes redirectAttributes) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Validate input
            if (title == null || title.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Course title is required.");
                return "redirect:/admin/courses/" + id + "/edit";
            }
            if (description == null || description.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Course description is required.");
                return "redirect:/admin/courses/" + id + "/edit";
            }
            if (title.length() < 3 || title.length() > 100) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Course title must be between 3 and 100 characters.");
                return "redirect:/admin/courses/" + id + "/edit";
            }
            if (description.length() < 10 || description.length() > 1000) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Course description must be between 10 and 1000 characters.");
                return "redirect:/admin/courses/" + id + "/edit";
            }

            User instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));

            course.setTitle(title.trim());
            course.setDescription(description.trim());
            course.setInstructor(instructor);
            course.setIsApproved(isApproved != null ? isApproved : false);

            courseRepository.save(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /**
     * Delete a course.
     */
    @PostMapping("/admin/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Check if course has enrollments
            if (!course.getEnrollments().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot delete course '" + course.getTitle() + "' as it has active enrollments.");
                return "redirect:/admin/courses";
            }

            courseRepository.delete(course);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Course '" + course.getTitle() + "' deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /**
     * Approve a course.
     */
    @PostMapping("/admin/courses/{id}/approve")
    public String approveCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            course.setIsApproved(true);
            courseRepository.save(course);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Course '" + course.getTitle() + "' approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /**
     * Disapprove a course.
     */
    @PostMapping("/admin/courses/{id}/disapprove")
    public String disapproveCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            course.setIsApproved(false);
            courseRepository.save(course);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Course '" + course.getTitle() + "' disapproved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to disapprove course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /**
     * Admin reports page.
     */
    @GetMapping("/admin/reports")
    public String adminReports(Model model) {
        // Generate basic reports
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        // Course enrollment statistics
        List<Course> popularCourses = courseRepository.findAll();

        model.addAttribute("title", "Reports");
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalEnrollments", totalEnrollments);
        model.addAttribute("popularCourses", popularCourses);
        return "admin/reports";
    }

    /**
     * Admin system settings page.
     */
    @GetMapping("/admin/settings")
    public String adminSettings(Model model) {
        model.addAttribute("title", "System Settings");
        return "admin/settings";
    }

    /**
     * Instructor dashboard.
     */
    @GetMapping("/instructor")
    public String instructorDashboard(Model model) {
        model.addAttribute("title", "Instructor Dashboard");
        return "instructor/dashboard";
    }

    /**
     * Instructor create course page.
     */
    @GetMapping("/instructor/create-course")
    public String instructorCreateCourse(Model model) {
        model.addAttribute("title", "Create Course");
        return "instructor/create-course";
    }

    /**
     * Instructor students page.
     */
    @GetMapping("/instructor/students")
    public String instructorStudents(Model model) {
        model.addAttribute("title", "My Students");
        return "instructor/students";
    }

    /**
     * Student dashboard.
     */
    @GetMapping("/student")
    public String studentDashboard(Model model) {
        model.addAttribute("title", "Student Dashboard");

        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        } else {
            model.addAttribute("username", null);
        }

        // Load some recommended courses for enrollment
        var recommendedCourses = courseService.getPublishedCourses(0, 6, "createdAt", "desc");
        model.addAttribute("recommendedCourses", recommendedCourses.getContent());

        return "student/dashboard";
    }

    /**
     * Profile page.
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "Profile");
        return "profile";
    }

    /**
     * Enroll in a course.
     */
    @PostMapping("/enroll/{courseId}")
    public String enrollInCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.enrollInCourse(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in the course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses";
    }

    /**
     * Unenroll from a course.
     */
    @PostMapping("/unenroll/{courseId}")
    public String unenrollFromCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.unenrollFromCourse(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully unenrolled from the course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses";
    }
}