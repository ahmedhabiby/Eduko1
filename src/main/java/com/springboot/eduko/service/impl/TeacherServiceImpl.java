package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.AuthTeacherResponse;
import com.springboot.eduko.controller.vms.TeacherData;
import com.springboot.eduko.controller.vms.UpdatableTeacher;
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

    @Override
    public UpdatableTeacher updateTeacher(AuthTeacherResponse authTeacherResponse) {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String email = Objects.requireNonNull(authentication).getName();
        BaseUser baseUser=baseUserRepo.findBaseUsersByEmail(email);
        BaseUser baseUser1=baseUserRepo.findBaseUsersByEmail(authTeacherResponse.getEmail());
        if(Objects.nonNull(baseUser1)&& !Objects.equals(baseUser,baseUser1))
            throw new RuntimeException("email.used");
        baseUser.setEmail(authTeacherResponse.getEmail());
        BaseUser returnedUser=baseUserRepo.save(baseUser);
        Teacher teacher = teacherRepo.getTeacherById(baseUser.getTeacher().getId());
        teacher.setTeacherName(authTeacherResponse.getTeacherName());
        Teacher returnedTeacher=teacherRepo.save(teacher);
        return new UpdatableTeacher(returnedTeacher.getId(),returnedUser.getEmail(),returnedTeacher.getTeacherName());
    }

}
