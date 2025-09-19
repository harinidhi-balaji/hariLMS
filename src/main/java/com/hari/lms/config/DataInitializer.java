package com.hari.lms.config;

import com.hari.lms.entity.Course;
import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import com.hari.lms.repository.CourseRepository;
import com.hari.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Data initialization component to create default users for demo purposes.
 * 
 * @author Hari Parthu
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@harilms.com");
            admin.setFullName("System Administrator");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);
            System.out.println("✅ Created default ADMIN user: admin / admin123");
        } else {
            // Update existing admin user password to ensure it's correct
            User admin = userRepository.findByUsername("admin").orElse(null);
            if (admin != null) {
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("🔄 Updated default ADMIN user password: admin / admin123");
            }
        }

        // Create default instructor user if not exists
        if (!userRepository.existsByUsername("instructor")) {
            User instructor = new User();
            instructor.setUsername("instructor");
            instructor.setEmail("instructor@harilms.com");
            instructor.setFullName("Dr. Rajesh Kumar");
            instructor.setPassword(passwordEncoder.encode("instructor123"));
            instructor.setRole(Role.INSTRUCTOR);
            instructor.setEnabled(true);
            instructor.setCreatedAt(LocalDateTime.now());
            instructor.setUpdatedAt(LocalDateTime.now());

            userRepository.save(instructor);
            System.out.println("✅ Created default INSTRUCTOR user: instructor / instructor123");
        } else {
            // Update existing instructor user password to ensure it's correct
            User instructor = userRepository.findByUsername("instructor").orElse(null);
            if (instructor != null) {
                instructor.setPassword(passwordEncoder.encode("instructor123"));
                instructor.setEnabled(true);
                userRepository.save(instructor);
                System.out.println("🔄 Updated default INSTRUCTOR user password: instructor / instructor123");
            }
        }

        // Create default student user if not exists
        if (!userRepository.existsByUsername("student")) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@harilms.com");
            student.setFullName("John Doe");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole(Role.STUDENT);
            student.setEnabled(true);
            student.setCreatedAt(LocalDateTime.now());
            student.setUpdatedAt(LocalDateTime.now());

            userRepository.save(student);
            System.out.println("✅ Created default STUDENT user: student / student123");
        } else {
            // Update existing student user password to ensure it's correct
            User student = userRepository.findByUsername("student").orElse(null);
            if (student != null) {
                student.setPassword(passwordEncoder.encode("student123"));
                student.setEnabled(true);
                userRepository.save(student);
                System.out.println("🔄 Updated default STUDENT user password: student / student123");
            }
        }

        System.out.println("\n🎓 HariLMS Demo Users Ready!");
        System.out.println("==================================");
        System.out.println("👑 ADMIN    : admin / admin123");
        System.out.println("👨‍🏫 INSTRUCTOR: instructor / instructor123");
        System.out.println("👨‍🎓 STUDENT   : student / student123");
        System.out.println("==================================\n");

        // Create sample courses
        createSampleCourses();
    }

    private void createSampleCourses() {
        User instructor = userRepository.findByUsername("instructor").orElse(null);
        if (instructor == null) {
            System.out.println("❌ Instructor user not found, skipping course creation");
            return;
        }

        // Create sample courses if they don't exist
        long courseCount = courseRepository.count();
        System.out.println("🔍 Current course count: " + courseCount);
        if (courseCount == 0) {
            // Course 1: Java Programming
            Course javaCourse = new Course();
            javaCourse.setTitle("Complete Java Programming Bootcamp");
            javaCourse.setDescription(
                    "Learn Java programming from scratch with hands-on projects, OOP concepts, and real-world applications.");
            javaCourse.setInstructor(instructor);
            javaCourse.setIsApproved(true);
            javaCourse.setCreatedAt(LocalDateTime.now());
            javaCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(javaCourse);

            // Course 2: Web Development
            Course webCourse = new Course();
            webCourse.setTitle("Full Stack Web Development");
            webCourse.setDescription(
                    "Master HTML, CSS, JavaScript, and backend technologies to build modern web applications.");
            webCourse.setInstructor(instructor);
            webCourse.setIsApproved(true);
            webCourse.setCreatedAt(LocalDateTime.now());
            webCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(webCourse);

            // Course 3: Data Science
            Course dataScienceCourse = new Course();
            dataScienceCourse.setTitle("Data Science with Python");
            dataScienceCourse.setDescription(
                    "Learn data analysis, visualization, and machine learning using Python, pandas, and scikit-learn.");
            dataScienceCourse.setInstructor(instructor);
            dataScienceCourse.setIsApproved(true);
            dataScienceCourse.setCreatedAt(LocalDateTime.now());
            dataScienceCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(dataScienceCourse);

            // Course 4: UI/UX Design
            Course uiuxCourse = new Course();
            uiuxCourse.setTitle("Complete UI/UX Design Course");
            uiuxCourse.setDescription(
                    "Learn UI/UX design from scratch with Figma, Adobe XD, user research, and design thinking.");
            uiuxCourse.setInstructor(instructor);
            uiuxCourse.setIsApproved(true);
            uiuxCourse.setCreatedAt(LocalDateTime.now());
            uiuxCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(uiuxCourse);

            // Course 5: Digital Marketing
            Course marketingCourse = new Course();
            marketingCourse.setTitle("Digital Marketing Mastery");
            marketingCourse.setDescription(
                    "Master SEO, Google Ads, Facebook Ads, social media marketing and more in this complete course.");
            marketingCourse.setInstructor(instructor);
            marketingCourse.setIsApproved(true);
            marketingCourse.setCreatedAt(LocalDateTime.now());
            marketingCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(marketingCourse);

            // Course 6: Mobile Development
            Course mobileCourse = new Course();
            mobileCourse.setTitle("React Native Mobile Development");
            mobileCourse.setDescription(
                    "Build cross-platform mobile apps for iOS and Android using React Native and JavaScript.");
            mobileCourse.setInstructor(instructor);
            mobileCourse.setIsApproved(true);
            mobileCourse.setCreatedAt(LocalDateTime.now());
            mobileCourse.setUpdatedAt(LocalDateTime.now());
            courseRepository.save(mobileCourse);

            System.out.println("📚 Created 6 sample courses for demonstration");
        } else {
            System.out.println("⏭️ Skipping course creation - " + courseCount + " courses already exist");
        }
    }
}