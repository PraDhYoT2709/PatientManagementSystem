package com.pms.auth.service;

import com.pms.auth.dto.JwtResponse;
import com.pms.auth.dto.LoginRequest;
import com.pms.auth.dto.RegisterRequest;
import com.pms.auth.entity.AuthProvider;
import com.pms.auth.entity.Role;
import com.pms.auth.entity.RoleName;
import com.pms.auth.entity.User;
import com.pms.auth.repository.RoleRepository;
import com.pms.auth.repository.UserRepository;
import com.pms.auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name(RoleName.PATIENT)
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedpassword")
                .provider(AuthProvider.LOCAL)
                .roles(roles)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        // Mock UserDetailsImpl
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getEmail()).thenReturn("test@example.com");
        when(userDetails.getAuthorities()).thenReturn(Set.of());

        // When
        JwtResponse result = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void registerUser_WithValidData_ShouldReturnUser() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");
        when(roleRepository.findByName(RoleName.PATIENT)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = authService.registerUser(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(AuthProvider.LOCAL, result.getProvider());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(registerRequest));
        assertEquals("Error: Username is already taken!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(registerRequest));
        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        User result = authService.getUserByEmail("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.getUserByEmail("nonexistent@example.com"));
        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        User result = authService.getUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByUsername_WhenUserNotExists_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.getUserByUsername("nonexistentuser"));
        assertEquals("User not found with username: nonexistentuser", exception.getMessage());
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = authService.existsByEmail("test@example.com");

        // Then
        assertTrue(result);
    }

    @Test
    void existsByEmail_WhenEmailNotExists_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = authService.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    void existsByUsername_WhenUsernameExists_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean result = authService.existsByUsername("testuser");

        // Then
        assertTrue(result);
    }

    @Test
    void existsByUsername_WhenUsernameNotExists_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByUsername("nonexistentuser")).thenReturn(false);

        // When
        boolean result = authService.existsByUsername("nonexistentuser");

        // Then
        assertFalse(result);
    }
}