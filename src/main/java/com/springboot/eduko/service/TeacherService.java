package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.AuthTeacherResponse;
import com.springboot.eduko.controller.vms.TeacherData;
import com.springboot.eduko.dtos.TeacherDto;

public interface TeacherService {
    TeacherDto saveTeacher(TeacherData teacherData);
    String getTeacherNameById(Long id);
    AuthTeacherResponse getLoginTeacher();

}
