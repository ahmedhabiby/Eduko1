package com.springboot.eduko.jwt;

import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.service.BaseUserService;
import com.springboot.eduko.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final BaseUserService baseUserService;
    @Autowired
    public CustomUserDetailsService(BaseUserService baseUserService) {
        this.baseUserService = baseUserService;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        BaseUserDto s = baseUserService.getUserByEmail(email);
        return new CustomUserDetails(s);
    }
}
