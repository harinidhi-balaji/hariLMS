package com.hari.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class for Hari Learning Management System.
 * 
 * @author Hari Parthu
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class LmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
        System.out.println("\n" +
                "=======================================================\n" +
                "   Hari LMS Application Started Successfully! 🚀\n" +
                "=======================================================\n" +
                "   🌐 Application URL: http://localhost:8080\n" +
                "   📚 Swagger UI: http://localhost:8080/swagger-ui.html\n" +
                "   📖 API Docs: http://localhost:8080/api-docs\n" +
                "=======================================================\n");
    }
}