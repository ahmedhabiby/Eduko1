package com.springboot.eduko.jwt;

import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.dtos.StudentDto;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final BaseUserDto baseUserDto;


    public CustomUserDetails(BaseUserDto baseUserDto) {

        this.baseUserDto=baseUserDto;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return baseUserDto.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role.getRole())).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return "{bcrypt}"+baseUserDto.getPassword();
    }

    @Override
    public String getUsername() {
        return baseUserDto.getEmail();
    }
}
