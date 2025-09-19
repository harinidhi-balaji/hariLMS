package com.hari.lms.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT authentication entry point to handle authentication errors.
 * 
 * @author Hari Parthu
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        logger.error("Unauthorized error: {}", authException.getMessage());

        String requestPath = request.getServletPath();
        String accept = request.getHeader("Accept");

        // If this is an API request or AJAX request, return JSON
        if (requestPath.startsWith("/api/") ||
                (accept != null && accept.contains("application/json")) ||
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", authException.getMessage());
            body.put("path", requestPath);
            body.put("timestamp", System.currentTimeMillis());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        } else {
            // For web requests, redirect to login page
            response.sendRedirect("/login?error=true");
        }
    }
}