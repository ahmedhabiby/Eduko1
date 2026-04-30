package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.*;
import com.springboot.eduko.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Tag(name = "Authentication", description = "Login, Register, Logout, Password Reset")
@RestController
public class SecurityController {

    private final AuthService authService;

    @Autowired
    public SecurityController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Student Register", description = "Register a new student account. parentName, parentNumber, studentNumber are optional.")
    @PostMapping({"/signup", "/auth/register"})
    public ResponseEntity<AuthResponse> signup(
            @RequestBody @Valid SignupRequestForStudent signupRequestForStudent) throws URISyntaxException {
        return ResponseEntity.created(new URI("/auth/register"))
                .body(authService.signupForStudent(signupRequestForStudent));
    }

    @Operation(summary = "Login", description = "Authenticate with email & password. Returns JWT token + user payload.")
    @PostMapping({"/login", "/auth/login"})
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) throws URISyntaxException {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "Teacher Register", description = "Register a new teacher account.")
    @PostMapping({"/signupForTeacher", "/auth/register/teacher"})
    public ResponseEntity<AuthResponse> signupForTeacher(
            @RequestBody @Valid SignupRequestForTeachers signupRequestForTeachers) throws URISyntaxException {
        return ResponseEntity.created(new URI("/auth/register/teacher"))
                .body(authService.signupForTeacher(signupRequestForTeachers));
    }

    @Operation(summary = "Logout", description = "Invalidates the current JWT token.")
    @PostMapping({"/logout", "/auth/logout"})
    public ResponseEntity<LogoutResponse> logout() throws URISyntaxException {
        return ResponseEntity.ok(authService.logout());
    }

    @Operation(summary = "Forgot Password (Step 1)", description = "Send email to get a reset token. In production the token is emailed; currently returned in response.")
    @PostMapping({"/forgot-password", "/auth/forgot-password"})
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @Operation(summary = "Reset Password (Step 2)", description = "Use the reset token from forgot-password to set a new password.")
    @PutMapping({"/reset-password", "/auth/reset-password"})
    public ResponseEntity<ResetPassword> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    /**
     * @deprecated Use /forgot-password then /reset-password instead.
     * This endpoint has a security issue — it resets password without verification.
     */
    @Deprecated
    @Operation(summary = "[Deprecated] Change Password", description = "Deprecated. Use /forgot-password + /reset-password flow instead.")
    @PutMapping("/resetPass")
    public ResponseEntity<ResetPassword> changePassword(@RequestBody RequestForNewPass requestForNewPass) throws URISyntaxException {
        return ResponseEntity.ok(authService.changePassword(requestForNewPass));
    }
}
