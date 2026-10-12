package com.example.image_processing_service.auth.security;

import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return User.withUsername(username)
                .password(appUser.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()))
                .build();
    }
}
