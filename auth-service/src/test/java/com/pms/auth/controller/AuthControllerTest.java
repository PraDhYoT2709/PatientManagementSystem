package com.pms.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.auth.dto.JwtResponse;
import com.pms.auth.dto.LoginRequest;
import com.pms.auth.dto.RegisterRequest;
import com.pms.auth.entity.AuthProvider;
import com.pms.auth.entity.Role;
import com.pms.auth.entity.RoleName;
import com.pms.auth.entity.User;
import com.pms.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        Set<String> roles = new HashSet<>();
        roles.add("PATIENT");

        JwtResponse jwtResponse = new JwtResponse("jwt-token", 1L, "testuser", "test@example.com", roles);

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void register_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .provider(AuthProvider.LOCAL)
                .build();

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully with username: testuser"));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password123");

        when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Error: Email is already in use!"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .provider(AuthProvider.LOCAL)
                .build();

        when(authService.getUserByEmail("test@example.com")).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/api/auth/user/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(authService.getUserByEmail("nonexistent@example.com"))
                .thenThrow(new RuntimeException("User not found with email: nonexistent@example.com"));

        // When & Then
        mockMvc.perform(get("/api/auth/user/nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkEmailExists_WhenEmailExists_ShouldReturnTrue() throws Exception {
        // Given
        when(authService.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/exists/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkEmailExists_WhenEmailNotExists_ShouldReturnFalse() throws Exception {
        // Given
        when(authService.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/exists/email/nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void checkUsernameExists_WhenUsernameExists_ShouldReturnTrue() throws Exception {
        // Given
        when(authService.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/exists/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkUsernameExists_WhenUsernameNotExists_ShouldReturnFalse() throws Exception {
        // Given
        when(authService.existsByUsername("nonexistentuser")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/exists/username/nonexistentuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}