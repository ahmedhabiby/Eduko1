package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.*;
import com.springboot.eduko.dtos.BaseUserDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    Response login(LoginRequest loginRequest);
    Response signupForStudent(SignupRequestForStudent signupRequestForStudent);
    Response signupForTeacher(SignupRequestForTeachers signupRequestForTeachers);
    LogoutResponse logout( );
    ResetPassword changePassword(RequestForNewPass requestForNewPass);

}
