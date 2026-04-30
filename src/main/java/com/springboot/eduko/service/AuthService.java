package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.*;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse signupForStudent(SignupRequestForStudent signupRequestForStudent);
    AuthResponse signupForTeacher(SignupRequestForTeachers signupRequestForTeachers);
    LogoutResponse logout();
    // Legacy — kept for backward compat, use forgotPassword + resetPassword flow instead
    ResetPassword changePassword(RequestForNewPass requestForNewPass);
    Object forgotPassword(ForgotPasswordRequest request);
    ResetPassword resetPassword(ResetPasswordRequest request);
}
