package com.example.image_processing_service.user.service;

import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.entity.Role;
import com.example.image_processing_service.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private AppUser user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setupUser() {
        user = new AppUser();
        user.setId(1L);
        user.setUsername("test-user");
        user.setRole(Role.USER);
    }

    private Jwt jwt(AppUser user) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("userID", user.getId())
                .subject(user.getUsername());
        return builder.build();
    }

    @Test
    void getUserFromJWT_returnsUser_whenUserExists() {

        Jwt jwt = jwt(user);

        when(userRepository.findByUsername("test-user")).thenReturn(Optional.of(user));

        AppUser result = userService.getUserFromJWT(jwt);

        assertThat(result)
                .isNotNull()
                .isEqualTo(user);

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void getUserFromJWT_throws_whenUserNotFound() {

        Jwt jwt = jwt(user);

        when(userRepository.findByUsername("test-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserFromJWT(jwt))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}