package com.example.image_processing_service.user.service;

import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public AppUser getUserFromJWT(Jwt jwt) {
        String username = jwt.getClaim("sub");

        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
