package com.pms.auth.security;

import com.pms.auth.entity.AuthProvider;
import com.pms.auth.entity.Role;
import com.pms.auth.entity.RoleName;
import com.pms.auth.entity.User;
import com.pms.auth.repository.RoleRepository;
import com.pms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("Error processing OAuth2 user", ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            user = registerNewUser(email, name);
        } else if (user.getProvider() != AuthProvider.GOOGLE) {
            throw new OAuth2AuthenticationException("User already registered with different provider");
        }

        return UserDetailsServiceImpl.UserPrincipal.create(user);
    }

    private User registerNewUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .username(name != null ? name : email.split("@")[0])
                .provider(AuthProvider.GOOGLE)
                .build();

        // Assign default role (PATIENT)
        Role patientRole = roleRepository.findByName(RoleName.PATIENT)
                .orElseThrow(() -> new RuntimeException("Patient role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(patientRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}