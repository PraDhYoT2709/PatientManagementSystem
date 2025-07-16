package com.prad.PMS.Controller;

import com.prad.PMS.Entity.*;
import com.prad.PMS.Repository.*;
import com.prad.PMS.Security.JwtUtil;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(AuthProvider.LOCAL);

//        Role role = roleRepository.findByName(RoleName.ROLE_PATIENT).orElseThrow();
//        user.setRoles(Set.of(role));
        Role role1 = roleRepository.findByName(RoleName.ROLE_DOCTOR).orElseThrow();
        user.setRoles(Set.of(role1));


        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.get("email"), payload.get("password")));

        String token = jwtUtil.generateToken(payload.get("email"));
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2LoginSuccess(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();  // usually the user’s email
        String token = jwtUtil.generateToken(email);  // your existing JWT generation
        return ResponseEntity.ok(Map.of("token", token));
    }

}