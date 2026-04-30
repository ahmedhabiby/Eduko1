package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.*;
import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class SecurityController {
    private final AuthService authService;

    @Autowired
    public SecurityController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@RequestBody @Valid SignupRequestForStudent signupRequestForStudent) throws URISyntaxException {
        return ResponseEntity.created(new URI("/signup")).body(authService.signupForStudent(signupRequestForStudent));
    }
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) throws URISyntaxException {
        return ResponseEntity.created(new URI("/login")).body(authService.login(loginRequest));
    }
    @PostMapping("/signupForTeacher")
    public ResponseEntity<Response> signupForTeacher(@RequestBody @Valid SignupRequestForTeachers signupRequestForTeachers) throws URISyntaxException {
        return ResponseEntity.created(new URI("/signupForTeacher")).body(authService.signupForTeacher(signupRequestForTeachers));
    }
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() throws URISyntaxException {
        return ResponseEntity.created(new URI("/logout")).body(authService.logout());
    }
    @PutMapping("/resetPass")
    public ResponseEntity<ResetPassword> changePassword(@RequestBody RequestForNewPass requestForNewPass) throws URISyntaxException {
        return ResponseEntity.created(new URI("/resetPass")).body(authService.changePassword(requestForNewPass));
    }

    }
