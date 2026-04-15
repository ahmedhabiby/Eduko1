package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.AuthStudentResponse;
import com.springboot.eduko.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
    private StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/auth/user")
    public ResponseEntity<AuthStudentResponse> authUser(){
        return ResponseEntity.ok(studentService.getAuthStudent());
    }
}
