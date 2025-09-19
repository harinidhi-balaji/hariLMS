package com.hari.lms.service;

import com.hari.lms.dto.request.LoginRequest;
import com.hari.lms.dto.request.SignupRequest;
import com.hari.lms.dto.response.JwtResponse;
import com.hari.lms.dto.response.UserResponse;
import com.hari.lms.entity.User;
import com.hari.lms.enums.Role;
import com.hari.lms.exception.ResourceNotFoundException;
import com.hari.lms.repository.UserRepository;
import com.hari.lms.security.UserDetailsImpl;
import com.hari.lms.security.jwt.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for authentication operations.
 * 
 * @author Hari Parthu
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Authenticate user and return JWT response.
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);
    }

    /**
     * Register a new user.
     */
    public UserResponse registerUser(SignupRequest signUpRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getFullName(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getRole());

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    /**
     * Check if username exists.
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get current authenticated user.
     */
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return modelMapper.map(user, UserResponse.class);
    }

    /**
     * Get current authenticated user entity.
     */
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Check if current user has specific role.
     */
    public boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.getAuthority()));
    }

    /**
     * Check if current user is admin.
     */
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Check if current user is instructor.
     */
    public boolean isInstructor() {
        return hasRole(Role.INSTRUCTOR);
    }

    /**
     * Check if current user is student.
     */
    public boolean isStudent() {
        return hasRole(Role.STUDENT);
    }
}