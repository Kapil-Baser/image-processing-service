package com.example.image_processing_service.auth.service;

import com.example.image_processing_service.auth.dto.AuthResponse;
import com.example.image_processing_service.auth.dto.LoginRequest;
import com.example.image_processing_service.auth.dto.RegisterRequest;
import com.example.image_processing_service.auth.security.MyUserDetails;
import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.entity.Role;
import com.example.image_processing_service.user.exception.UsernameAlreadyExistsException;
import com.example.image_processing_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.username());
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.username());
        appUser.setPasswordHash(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.USER);

        userRepository.saveAndFlush(appUser);

        String token = tokenService.generateToken(appUser);

        return new AuthResponse(appUser.getUsername(), token);
    }

    public AuthResponse authenticate(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

        AppUser appUser = myUserDetails.getAppUser();

        String token = tokenService.generateToken(appUser);

        return new AuthResponse(request.username(), token);
    }
}
