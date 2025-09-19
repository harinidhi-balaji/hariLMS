package com.hari.lms.controller;

import com.hari.lms.dto.request.LoginRequest;
import com.hari.lms.dto.request.SignupRequest;
import com.hari.lms.dto.response.JwtResponse;
import com.hari.lms.dto.response.UserResponse;
import com.hari.lms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * 
 * @author Hari Parthu
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Authenticate user and return JWT token.
     */
    @PostMapping("/signin")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    /**
     * Register a new user.
     */
    @PostMapping("/signup")
    @Operation(summary = "User Registration", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            UserResponse userResponse = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    /**
     * Get current authenticated user information.
     */
    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get information about the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse userResponse = authService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Check if username is available.
     */
    @GetMapping("/check-username")
    @Operation(summary = "Check Username", description = "Check if username is available for registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username availability checked"),
            @ApiResponse(responseCode = "400", description = "Invalid username parameter")
    })
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = authService.existsByUsername(username);
        return ResponseEntity.ok(!exists); // Return true if available (not exists)
    }

    /**
     * Check if email is available.
     */
    @GetMapping("/check-email")
    @Operation(summary = "Check Email", description = "Check if email is available for registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email availability checked"),
            @ApiResponse(responseCode = "400", description = "Invalid email parameter")
    })
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = authService.existsByEmail(email);
        return ResponseEntity.ok(!exists); // Return true if available (not exists)
    }
}