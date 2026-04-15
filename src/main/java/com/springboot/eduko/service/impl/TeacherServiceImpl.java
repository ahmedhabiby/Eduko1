package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.AuthTeacherResponse;
import com.springboot.eduko.controller.vms.TeacherData;
import com.springboot.eduko.dtos.TeacherDto;
import com.springboot.eduko.mapper.TeacherMapper;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.model.Teacher;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.TeacherRepo;
import com.springboot.eduko.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TeacherServiceImpl implements TeacherService {
    private TeacherMapper teacherMapper;
    private TeacherRepo teacherRepo;
    private BaseUserRepo baseUserRepo;

    @Autowired
    private TeacherServiceImpl(TeacherMapper teacherMapper,TeacherRepo teacherRepo,BaseUserRepo baseUserRepo){

        this.teacherMapper=teacherMapper;
        this.teacherRepo=teacherRepo;
        this.baseUserRepo=baseUserRepo;
    }
    @Override
    public TeacherDto saveTeacher(TeacherData teacherData) {
        TeacherDto teacherDto =new TeacherDto();
        teacherDto.setTeacherName(teacherData.getTeacherName());
        Teacher teacher =teacherMapper.toEntity(teacherDto);
        return teacherMapper.toDto(teacherRepo.save(teacher));
    }

    @Override
    public String getTeacherNameById(Long id) {
        Teacher teacher =teacherRepo.getTeacherById(id);
        if (Objects.isNull(teacher))
            throw new RuntimeException("Student.not.found");
        return teacherMapper.toDto(teacher).getTeacherName();
    }

    @Override
    public AuthTeacherResponse getLoginTeacher() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        BaseUser baseUser = baseUserRepo.findBaseUsersByEmail(email);

        if (baseUser == null) {
            throw new RuntimeException("user.not.found");
        }

        if (baseUser.getTeacher() == null) {
            throw new RuntimeException("teacher.not.found");
        }

        Teacher teacher = teacherRepo.getTeacherById(baseUser.getTeacher().getId());

        if (teacher == null) {
            throw new RuntimeException("teacher.not.found");
        }

        return new AuthTeacherResponse(
                baseUser.getEmail(),
                teacher.getTeacherName()
        );
    }

}
