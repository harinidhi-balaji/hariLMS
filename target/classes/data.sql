-- Hari LMS Seed Data
-- This file contains sample data to quickly test the LMS application

-- Insert sample users with encrypted passwords matching login page
-- Note: These passwords are BCrypt encoded versions
INSERT INTO users (username, email, full_name, password, role, enabled, created_at, updated_at) VALUES 
-- Admin user (username: admin, password: admin123)
('admin', 'admin@harilms.com', 'Administrator', '$2a$10$N7IMw1WGUhF6NfHNTXqYneSyYKsYLqNKlnqZhBJDJT9KX5Oe6vY/a', 'ADMIN', true, NOW(), NOW()),

-- Instructor user (username: instructor, password: instructor123) - matches login page
('instructor', 'instructor@harilms.com', 'Dr. Rajesh Kumar', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'INSTRUCTOR', true, NOW(), NOW()),

-- Student user (username: student, password: student123) - matches login page  
('student', 'student@harilms.com', 'John Doe', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'STUDENT', true, NOW(), NOW()),

-- Additional student for testing
('student2', 'student2@harilms.com', 'Jane Smith', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'STUDENT', true, NOW(), NOW());

-- Insert sample courses
INSERT INTO courses (title, description, price, is_approved, instructor_id, created_at, updated_at) VALUES 
-- Course 1: Published/Approved
('Introduction to Java Programming', 
 'Learn the fundamentals of Java programming language including syntax, OOP concepts, and basic data structures. This comprehensive course covers everything from variables and loops to classes and inheritance.',
 49.99, true, 2, NOW(), NOW()),

-- Course 2: Published/Approved
('Spring Boot Fundamentals', 
 'Master Spring Boot framework for building modern web applications. Topics include dependency injection, REST APIs, database integration, and security implementation.',
 79.99, true, 2, NOW(), NOW()),

-- Course 3: Draft/Not Approved (for testing instructor workflow)
('Advanced Database Design', 
 'Deep dive into database design principles, normalization, indexing, and performance optimization. Covers both SQL and NoSQL database technologies.',
 99.99, false, 2, NOW(), NOW()),

-- Course 4: Pending/Not Approved (for testing admin approval workflow)
('Web Development with React', 
 'Build modern, interactive web applications using React.js. Learn components, state management, hooks, and integration with backend APIs.',
 59.99, false, 2, NOW(), NOW()),

-- Course 5: Published/Approved
('Python for Data Science',
 'Comprehensive introduction to Python programming for data analysis and machine learning. Covers pandas, numpy, matplotlib, and scikit-learn libraries.',
 89.99, true, 2, NOW(), NOW()),

-- Course 6: Published/Approved
('Machine Learning Fundamentals',
 'Understanding core machine learning concepts including supervised and unsupervised learning, regression, classification, and neural networks.',
 149.99, true, 2, NOW(), NOW()),

-- Course 7: Published/Approved
('JavaScript ES6+ Modern Development',
 'Master modern JavaScript features including arrow functions, destructuring, async/await, modules, and advanced array methods.',
 39.99, true, 2, NOW(), NOW()),

-- Course 8: Published/Approved
('Docker and Containerization',
 'Learn containerization concepts, Docker fundamentals, container orchestration, and deployment strategies for modern applications.',
 69.99, true, 2, NOW(), NOW()),

-- Course 9: Published/Approved
('AWS Cloud Practitioner',
 'Introduction to Amazon Web Services covering EC2, S3, RDS, Lambda, and essential cloud computing concepts for beginners.',
 199.99, true, 2, NOW(), NOW()),

-- Course 10: Published/Approved
('Git Version Control Mastery',
 'Complete guide to Git version control system including branching, merging, rebasing, and collaborative development workflows.',
 29.99, true, 2, NOW(), NOW()),

-- Course 11: Published/Approved
('Node.js Backend Development',
 'Build robust backend applications using Node.js, Express.js, MongoDB, and RESTful API design principles.',
 89.99, true, 2, NOW(), NOW()),

-- Course 12: Published/Approved
('Angular Framework Complete Guide',
 'Comprehensive Angular framework course covering components, services, routing, forms, and state management with NgRx.',
 109.99, true, 2, NOW(), NOW()),

-- Course 13: Published/Approved
('Cybersecurity Fundamentals',
 'Essential cybersecurity concepts including network security, cryptography, risk assessment, and security best practices.',
 129.99, true, 2, NOW(), NOW()),

-- Course 14: Published/Approved
('Mobile App Development with Flutter',
 'Create cross-platform mobile applications using Flutter framework with Dart programming language.',
 119.99, true, 2, NOW(), NOW()),

-- Course 15: Published/Approved
('DevOps Engineering Essentials',
 'Learn DevOps practices including CI/CD pipelines, infrastructure as code, monitoring, and automation tools.',
 159.99, true, 2, NOW(), NOW()),

-- Course 16: Published/Approved
('UI/UX Design Principles',
 'Master user interface and user experience design principles, prototyping, wireframing, and design thinking methodologies.',
 79.99, true, 2, NOW(), NOW()),

-- Course 17: Published/Approved
('Blockchain and Cryptocurrency',
 'Understanding blockchain technology, smart contracts, cryptocurrency fundamentals, and decentralized application development.',
 189.99, true, 2, NOW(), NOW());

-- Insert sample lessons for Course 1 (Introduction to Java Programming)
INSERT INTO lessons (course_id, sequence_number, title, content_type, content_url, content_text, created_at, updated_at) VALUES 
-- Java Basics
(1, 0, 'Getting Started with Java', 'TEXT', NULL, 
 'Welcome to Java programming! Java is a powerful, object-oriented programming language that is platform-independent. In this lesson, we will cover:\n\n1. What is Java?\n2. Installing Java Development Kit (JDK)\n3. Setting up your development environment\n4. Writing your first Java program\n\nJava follows the principle of "Write Once, Run Anywhere" (WORA), making it an excellent choice for cross-platform development.',
 NOW(), NOW()),

(1, 1, 'Java Syntax and Variables', 'TEXT', NULL,
 'Understanding Java syntax is crucial for writing effective programs. Key concepts include:\n\n- Data types (int, double, String, boolean)\n- Variable declaration and initialization\n- Naming conventions\n- Comments (single-line and multi-line)\n- Basic operators\n\nExample:\nint age = 25;\nString name = "John Doe";\nboolean isStudent = true;',
 NOW(), NOW()),

(1, 2, 'Control Structures - Video Tutorial', 'VIDEO', 'https://example.com/java-control-structures-video', NULL, NOW(), NOW()),

(1, 3, 'Object-Oriented Programming Concepts', 'TEXT', NULL,
 'Java is built around Object-Oriented Programming (OOP) principles:\n\n1. Classes and Objects\n2. Encapsulation\n3. Inheritance\n4. Polymorphism\n5. Abstraction\n\nThese concepts help organize code into reusable, maintainable structures.',
 NOW(), NOW()),

(1, 4, 'Java Reference Guide', 'PDF', 'https://example.com/java-reference-guide.pdf', NULL, NOW(), NOW());

-- Insert sample lessons for Course 2 (Spring Boot Fundamentals)
INSERT INTO lessons (course_id, sequence_number, title, content_type, content_url, content_text, created_at, updated_at) VALUES 
(2, 0, 'Introduction to Spring Framework', 'TEXT', NULL,
 'Spring Framework is a comprehensive programming and configuration model for modern Java-based enterprise applications. Key features include:\n\n- Dependency Injection (DI)\n- Aspect-Oriented Programming (AOP)\n- Integration with various technologies\n- Simplified database access\n\nSpring Boot builds on top of Spring Framework to provide auto-configuration and rapid application development.',
 NOW(), NOW()),

(2, 1, 'Creating Your First Spring Boot Application', 'VIDEO', 'https://example.com/first-spring-boot-app', NULL, NOW(), NOW()),

(2, 2, 'REST API Development', 'TEXT', NULL,
 'Building REST APIs with Spring Boot involves:\n\n- Using @RestController annotation\n- Mapping HTTP methods (@GetMapping, @PostMapping, etc.)\n- Request and Response handling\n- Data validation\n- Error handling\n\nExample:\n@RestController\n@RequestMapping("/api/users")\npublic class UserController {\n    // API endpoints here\n}',
 NOW(), NOW()),

(2, 3, 'Spring Boot Configuration Guide', 'PDF', 'https://example.com/spring-boot-config.pdf', NULL, NOW(), NOW());

-- Insert sample enrollments
INSERT INTO enrollments (student_id, course_id, enrollment_date, progress, completed_lessons) VALUES 
-- Student 1 enrollments
(3, 1, NOW(), 75, '["1", "2", "3"]'),  -- 75% progress in Java course, completed 3 lessons
(3, 2, NOW() - INTERVAL '30 days', 100, '["1", "2", "3", "4"]'),  -- Completed Spring Boot course

-- Student 2 enrollments  
(4, 1, NOW(), 25, '["1"]'),  -- 25% progress in Java course, completed 1 lesson
(4, 2, NOW() - INTERVAL '15 days', 50, '["1", "2"]');  -- 50% progress in Spring Boot course, completed 2 lessons