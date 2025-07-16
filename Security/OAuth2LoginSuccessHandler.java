package com.prad.PMS.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prad.PMS.Entity.AuthProvider;
import com.prad.PMS.Entity.Role;
import com.prad.PMS.Entity.RoleName;
import com.prad.PMS.Entity.User;
import com.prad.PMS.Repository.RoleRepository;
import com.prad.PMS.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Make sure it's injected

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setPassword(""); // Or generate random/dummy password if needed
            newUser.setProvider(AuthProvider.GOOGLE);

            // Assign default role
            Role defaultRole = roleRepository.findByName(RoleName.ROLE_PATIENT)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            newUser.setRoles(Set.of(defaultRole));

            return userRepository.save(newUser);
        });

        String token = jwtUtil.generateToken(user.getEmail());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("jwt", token)));
    }
}
