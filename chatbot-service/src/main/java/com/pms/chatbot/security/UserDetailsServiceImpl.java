package com.pms.chatbot.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // For chatbot service, we create a simple user with basic authorities
        // In a real implementation, you might want to validate the user against auth service
        return User.withUsername(username)
                .password("")
                .authorities("ROLE_USER")
                .build();
    }
}