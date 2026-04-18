package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.AuthStudentResponse;
import com.springboot.eduko.controller.vms.StudentData;
import com.springboot.eduko.controller.vms.UpdatableStudent;
import com.springboot.eduko.dtos.StudentDto;

public interface StudentService {
    StudentDto  saveStudent(StudentData studentData);
    String getStudentNameById(Long id);
    AuthStudentResponse getAuthStudent();
    UpdatableStudent updateStudent(AuthStudentResponse authStudentResponse);
}
