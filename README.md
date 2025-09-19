# Hari Learning Management System (LMS)

A production-ready Learning Management System built with **Spring Boot**, **MySQL**, **JWT Authentication**, **Thymeleaf**, and **TailwindCSS**. This LMS supports role-based access control with Admin, Instructor, and Student roles.

## 🚀 Features

### 🔐 Authentication & Authorization

- **JWT-based authentication** with secure token management
- **Role-Based Access Control (RBAC)** with three distinct roles:
  - **Admin**: Manage users, approve/reject courses, view system analytics
  - **Instructor**: Create/edit courses, add lessons, submit for approval, view enrolled students
  - **Student**: Browse published courses, enroll in courses, track progress, view lessons

### 📚 Course Management

- **Complete course lifecycle** from draft to published
- **Multi-content lesson support**: Text, Video, and PDF content types
- **Course approval workflow**: Instructors submit → Admins approve/reject
- **Progress tracking** with completion percentages
- **Duplicate enrollment prevention**
- **Paginated course listings** with search functionality

### 🎨 Modern UI/UX

- **Responsive design** built with TailwindCSS
- **Role-based dashboard views** with personalized navigation
- **Clean, professional interface** with intuitive user experience
- **Mobile-friendly** design for all device types

### 🛠️ Technical Features

- **RESTful API** with comprehensive Swagger documentation
- **Modular architecture** with separation of concerns
- **Data validation** with comprehensive error handling
- **Unit & integration tests** with JUnit and MockMVC
- **Docker containerization** for easy deployment
- **Database seeding** with sample data for quick testing

## 📋 Tech Stack

| Category             | Technology                                          |
| -------------------- | --------------------------------------------------- |
| **Backend**          | Spring Boot 3.1.5, Spring Security, Spring Data JPA |
| **Frontend**         | Thymeleaf, TailwindCSS, JavaScript                  |
| **Database**         | MySQL 8.0                                           |
| **Authentication**   | JWT (JSON Web Tokens)                               |
| **Documentation**    | Swagger/OpenAPI 3                                   |
| **Testing**          | JUnit 5, MockMVC, Mockito                           |
| **Build Tool**       | Maven                                               |
| **Containerization** | Docker, Docker Compose                              |

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/hari/lms/
│   │   ├── config/          # Security, ModelMapper configurations
│   │   ├── controller/      # REST controllers and web controllers
│   │   ├── dto/            # Request/Response DTOs
│   │   │   ├── request/    # Request DTOs
│   │   │   └── response/   # Response DTOs
│   │   ├── entity/         # JPA entities (User, Course, Lesson, Enrollment)
│   │   ├── enums/          # Enumerations (Role, CourseStatus, ContentType)
│   │   ├── exception/      # Custom exceptions and global handler
│   │   ├── repository/     # JPA repositories
│   │   ├── security/       # Security implementations (JWT, UserDetails)
│   │   │   └── jwt/        # JWT utilities and filters
│   │   └── service/        # Business logic services
│   └── resources/
│       ├── templates/      # Thymeleaf templates
│       ├── static/         # Static resources
│       ├── application.yml # Application configuration
│       └── data.sql       # Database seed data
└── test/                   # Unit and integration tests
```

## 🚦 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0** (or use Docker)
- **Docker** (optional, for containerized deployment)

### Option 1: Local Development Setup

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd hari-lms
   ```

2. **Setup MySQL Database**

   ```sql
   CREATE DATABASE hari_lms_db;
   CREATE USER 'lms_user'@'localhost' IDENTIFIED BY 'lms_password';
   GRANT ALL PRIVILEGES ON hari_lms_db.* TO 'lms_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure Application Properties**
   Update `src/main/resources/application.yml` with your database credentials:

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/hari_lms_db?createDatabaseIfNotExist=true&serverTimezone=UTC
       username: lms_user
       password: lms_password
   ```

4. **Build and Run**

   ```bash
   # Make the Maven wrapper executable
   chmod +x mvnw

   # Build the project
   ./mvnw clean package -DskipTests

   # Run the application
   ./mvnw spring-boot:run
   ```

5. **Access the Application**
   - **Web Application**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **API Documentation**: http://localhost:8080/api-docs

### Option 2: Docker Deployment

1. **Run with Docker Compose**

   ```bash
   # Start the application with MySQL
   docker-compose up -d

   # To include phpMyAdmin (development only)
   docker-compose --profile dev up -d
   ```

2. **Access Services**

   - **LMS Application**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **phpMyAdmin** (dev profile): http://localhost:8081

3. **Stop Services**
   ```bash
   docker-compose down
   ```

## 👥 Default User Accounts

The application comes with pre-configured test accounts:

| Role           | Username      | Email                   | Password      |
| -------------- | ------------- | ----------------------- | ------------- |
| **Admin**      | `admin`       | admin@harilms.com       | `admin123`    |
| **Instructor** | `instructor1` | instructor1@harilms.com | `password123` |
| **Student**    | `student1`    | student1@harilms.com    | `password123` |
| **Student**    | `student2`    | student2@harilms.com    | `password123` |

## 📖 API Documentation

### Authentication Endpoints

- **POST** `/api/auth/signin` - User login
- **POST** `/api/auth/signup` - User registration
- **GET** `/api/auth/me` - Get current user info
- **GET** `/api/auth/check-username?username=xxx` - Check username availability
- **GET** `/api/auth/check-email?email=xxx` - Check email availability

### Course Management Endpoints

- **GET** `/api/courses` - Get all courses (paginated)
- **GET** `/api/courses/published` - Get published courses
- **GET** `/api/courses/{id}` - Get course by ID
- **POST** `/api/courses` - Create new course (Instructor/Admin)
- **PUT** `/api/courses/{id}` - Update course (Instructor/Admin)
- **DELETE** `/api/courses/{id}` - Delete course (Instructor/Admin)
- **PUT** `/api/courses/{id}/submit` - Submit course for approval
- **PUT** `/api/courses/{id}/approve` - Approve course (Admin only)
- **PUT** `/api/courses/{id}/reject` - Reject course (Admin only)
- **GET** `/api/courses/pending` - Get pending courses (Admin only)
- **GET** `/api/courses/my-courses` - Get instructor's courses
- **GET** `/api/courses/search?q=xxx` - Search courses

### Request/Response Examples

**Login Request:**

```json
{
  "username": "student1",
  "password": "password123"
}
```

**JWT Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 3,
  "username": "student1",
  "email": "student1@harilms.com",
  "roles": ["ROLE_STUDENT"]
}
```

**Create Course Request:**

```json
{
  "title": "Introduction to Python Programming",
  "description": "Learn Python programming from scratch with hands-on examples and projects."
}
```

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CourseServiceTest

# Run tests with coverage report
./mvnw test jacoco:report
```

### Test Coverage

The project includes:

- **Unit tests** for services and controllers
- **Integration tests** for repository layers
- **Security tests** for authentication flows
- **API tests** with MockMVC

## 🚀 Deployment

### Production Configuration

1. **Update application-prod.yml**

   ```yaml
   spring:
     datasource:
       url: ${DATABASE_URL}
       username: ${DATABASE_USERNAME}
       password: ${DATABASE_PASSWORD}
     jpa:
       hibernate:
         ddl-auto: validate

   jwt:
     secret: ${JWT_SECRET}
     expiration: ${JWT_EXPIRATION:86400000}
   ```

2. **Set Environment Variables**

   ```bash
   export DATABASE_URL=jdbc:mysql://production-db:3306/lms_db
   export DATABASE_USERNAME=lms_user
   export DATABASE_PASSWORD=secure_password
   export JWT_SECRET=your-256-bit-secret-key
   ```

3. **Build Production Image**
   ```bash
   docker build -t hari-lms:latest .
   ```

### Database Migration

For production deployments, consider using Flyway or Liquibase for database migrations:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

## 🔧 Configuration

### Key Configuration Properties

| Property                        | Description              | Default          |
| ------------------------------- | ------------------------ | ---------------- |
| `jwt.secret`                    | JWT signing secret       | `mySecretKey...` |
| `jwt.expiration`                | JWT expiration time (ms) | `86400000` (24h) |
| `spring.jpa.hibernate.ddl-auto` | Database schema strategy | `create-drop`    |
| `spring.sql.init.mode`          | SQL initialization mode  | `always`         |

### Security Configuration

The application uses Spring Security with:

- **JWT token-based authentication**
- **RBAC with method-level security**
- **CORS support** for API endpoints
- **CSRF protection** for web forms

## 📊 Sample Data

The application includes sample data:

- **1 Admin user** with system management privileges
- **1 Instructor user** with 4 sample courses (various statuses)
- **2 Student users** with enrollment and progress data
- **Sample lessons** with different content types (Text, Video, PDF)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **TailwindCSS** for the utility-first CSS framework
- **Thymeleaf** for the modern server-side templating
- **JWT.io** for authentication standards

## 📞 Support

For support, email hari.parthu@example.com or create an issue in the repository.

---

**Made with ❤️ by Hari Parthu**

_Happy Learning! 📚_
