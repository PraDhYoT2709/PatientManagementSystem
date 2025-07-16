package com.prad.PMS.Service;

import com.prad.PMS.Entity.AuthProvider;
import com.prad.PMS.Entity.Role;
import com.prad.PMS.Entity.RoleName;
import com.prad.PMS.Entity.User;
import com.prad.PMS.Repository.RoleRepository;
import com.prad.PMS.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // check if user exists
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Role role = roleRepository.findByName(RoleName.ROLE_PATIENT)
                    .orElseThrow(() -> new RuntimeException("ROLE_PATIENT not found"));

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProvider(AuthProvider.valueOf("GOOGLE"));
            newUser.setRoles(Set.of(role));
            return userRepository.save(newUser);
        });

        return new DefaultOAuth2User(
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                        .toList(),
                oauthUser.getAttributes(),
                "email"
        );
    }
}
