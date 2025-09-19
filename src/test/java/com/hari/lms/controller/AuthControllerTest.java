package com.hari.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hari.lms.dto.request.LoginRequest;
import com.hari.lms.dto.request.SignupRequest;
import com.hari.lms.dto.response.JwtResponse;
import com.hari.lms.dto.response.UserResponse;
import com.hari.lms.enums.Role;
import com.hari.lms.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController.
 * 
 * @author Hari Parthu
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private JwtResponse jwtResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        loginRequest = new LoginRequest("testuser", "password");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@test.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole(Role.STUDENT);

        jwtResponse = new JwtResponse("test-token", 1L, "testuser", "test@test.com",
                Arrays.asList("ROLE_STUDENT"));

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@test.com");
        userResponse.setRole(Role.STUDENT);
        userResponse.setEnabled(true);
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void signin_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void signin_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given - empty request
        LoginRequest invalidRequest = new LoginRequest("", "");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_WithValidData_ShouldReturnUserResponse() throws Exception {
        // Given
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void signup_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - invalid email
        signupRequest.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")
    void getCurrentUser_WhenAuthenticated_ShouldReturnUserInfo() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getCurrentUser_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void checkUsername_WithAvailableUsername_ShouldReturnTrue() throws Exception {
        // Given
        when(authService.existsByUsername("newuser")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkUsername_WithTakenUsername_ShouldReturnFalse() throws Exception {
        // Given
        when(authService.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void checkEmail_WithAvailableEmail_ShouldReturnTrue() throws Exception {
        // Given
        when(authService.existsByEmail("new@test.com")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "new@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkEmail_WithTakenEmail_ShouldReturnFalse() throws Exception {
        // Given
        when(authService.existsByEmail("existing@test.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "existing@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}