package com.example.image_processing_service.auth.model;

import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class MyUserDetails implements UserDetails {

    private final transient AppUser appUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = appUser.getRole();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return List.of(authority);
    }

    @Override
    public @Nullable String getPassword() {
        return appUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }
}
