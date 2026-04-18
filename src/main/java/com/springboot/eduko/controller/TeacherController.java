package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.AuthTeacherResponse;
import com.springboot.eduko.controller.vms.UpdatableTeacher;
import com.springboot.eduko.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeacherController {
    private TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/auth/teacher")
    public ResponseEntity<AuthTeacherResponse> authTeacher(){
        return ResponseEntity.ok(teacherService.getLoginTeacher());
    }
    @PutMapping("/update/teacher")
    public ResponseEntity<UpdatableTeacher> updateTeacher(@RequestBody AuthTeacherResponse authTeacherResponse){
        return ResponseEntity.ok(teacherService.updateTeacher(authTeacherResponse));
    }
    
}
