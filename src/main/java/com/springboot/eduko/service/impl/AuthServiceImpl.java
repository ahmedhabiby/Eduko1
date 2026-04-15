package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.*;
import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.dtos.TeacherDto;
import com.springboot.eduko.jwt.HandleToken;
import com.springboot.eduko.mapper.BaseUserMapper;
import com.springboot.eduko.mapper.StudentMapper;
import com.springboot.eduko.mapper.TeacherMapper;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.EduRoles;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.RoleRepo;
import com.springboot.eduko.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {
    private final BaseUserService baseUserService;
    private final BaseUserMapper baseUserMapper;
    private final BaseUserRepo baseUserRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final HandleToken handleToken;
    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;
    private final TokenBlackListService tokenBlackListService;
    private HttpServletRequest httpServletRequest;


    @Autowired
    public AuthServiceImpl(HandleToken handleToken,HttpServletRequest httpServletRequest,TeacherMapper teacherMapper,TokenBlackListService tokenBlackListService,TeacherService teacherService,StudentMapper studentMapper,  RoleRepo roleRepo, PasswordEncoder passwordEncoder,BaseUserMapper baseUserMapper,BaseUserService baseUserService,BaseUserRepo baseUserRepo,StudentService studentService) {
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.handleToken = handleToken;
        this.baseUserMapper=baseUserMapper;
        this.baseUserRepo=baseUserRepo;
        this.baseUserService=baseUserService;
        this.studentService=studentService;
        this.studentMapper=studentMapper;
        this.teacherService=teacherService;
        this.teacherMapper=teacherMapper;
        this.tokenBlackListService=tokenBlackListService;
        this.httpServletRequest=httpServletRequest;
    }
    @Override
    public Response login(LoginRequest loginRequest) {
       BaseUserDto baseUserDto=baseUserService.getUserByEmail(loginRequest.getEmail());
       if(Objects.isNull(baseUserDto))
           throw new RuntimeException("email.not.exist");
        String name="";
        if (baseUserDto.getStudent() != null && baseUserDto.getStudent().getId() != null) {
            name = studentService.getStudentNameById(baseUserDto.getStudent().getId());
        } else if (baseUserDto.getTeacher() != null && baseUserDto.getTeacher().getId() != null) {
            name = teacherService.getTeacherNameById(baseUserDto.getTeacher().getId());
        }

        if(Objects.isNull(baseUserDto))
           throw new RuntimeException("Student.not.found");
       if(!(passwordEncoder.matches(loginRequest.getPassword(),baseUserDto.getPassword().replace("{bcrypt}",""))))
           throw new RuntimeException("Invalid.password");
       return new Response(name,handleToken.generateToken(baseUserDto));
    }

    @Override
    public Response signupForStudent(SignupRequestForStudent signupRequestForStudent) {
        BaseUserDto baseUserDto=new BaseUserDto();
        BaseUserDto baseUserDto1= baseUserService.getUserByEmail(signupRequestForStudent.getEmail());
        if(Objects.nonNull(baseUserDto1))
            throw new RuntimeException("Student.already.exist");
        baseUserDto.setEmail(signupRequestForStudent.getEmail());
        baseUserDto.setPassword(passwordEncoder.encode(signupRequestForStudent.getPassword()));
        EduRoles roles =roleRepo.findByRoleId(3L);
        if(Objects.isNull(roles))
            throw new RuntimeException("Role.not.found");
        BaseUser baseUser = baseUserMapper.toEntity(baseUserDto);
        if(Objects.isNull(roles))
            throw new RuntimeException("Role.not.found");
        BaseUser baseUser1 = baseUserMapper.toEntity(baseUserDto);
        baseUser1.setRoles(List.of(roles));
        StudentData studentData =new StudentData(signupRequestForStudent.getFirstName(), signupRequestForStudent.getLastName(), signupRequestForStudent.getParentName(), signupRequestForStudent.getParentNumber(), signupRequestForStudent.getStudentNumber());
        StudentDto studentDto =studentService.saveStudent(studentData);
        baseUser1.setStudent(studentMapper.toEntity(studentDto));
        BaseUser baseUser2=baseUserRepo.save(baseUser1);
        BaseUserDto baseUserDto2=baseUserMapper.toDto(baseUser2);
        return new Response(baseUserDto2.getStudent().getFirstName(),handleToken.generateToken(baseUserDto2));

    }

    @Override
    public Response signupForTeacher(SignupRequestForTeachers signupRequestForTeachers) {
        BaseUserDto baseUserDto=new BaseUserDto();
        BaseUserDto baseUserDto1= baseUserService.getUserByEmail(signupRequestForTeachers.getEmail());
        if(Objects.nonNull(baseUserDto1))
            throw new RuntimeException("teacher.already.exist");
        baseUserDto.setEmail(signupRequestForTeachers.getEmail());
        baseUserDto.setPassword(passwordEncoder.encode(signupRequestForTeachers.getPassword()));
        EduRoles roles =roleRepo.findByRoleId(2L);
        if(Objects.isNull(roles))
            throw new RuntimeException("Role.not.found");
        BaseUser baseUser = baseUserMapper.toEntity(baseUserDto);
        if(Objects.isNull(roles))
            throw new RuntimeException("Role.not.found");
        BaseUser baseUser1 = baseUserMapper.toEntity(baseUserDto);
        baseUser1.setRoles(List.of(roles));
        TeacherData teacherData=new TeacherData(signupRequestForTeachers.getTeacherName());
        TeacherDto teacherDto =teacherService.saveTeacher(teacherData);
        baseUser1.setTeacher(teacherMapper.toEntity(teacherDto));
        BaseUser baseUser2=baseUserRepo.save(baseUser1);
        BaseUserDto baseUserDto2=baseUserMapper.toDto(baseUser2);
        return new Response(baseUserDto2.getTeacher().getTeacherName(),handleToken.generateToken(baseUserDto2));
    }
    @Override
    public LogoutResponse logout() {
        String header=httpServletRequest.getHeader("Authorization");
        if(header!=null&&header.startsWith("Bearer ")){
            String token = header.substring(7);
            Date expireDate = handleToken.getExpireDateFromToken(token);
            tokenBlackListService.saveTokenBlackList(token,expireDate);
        }
        return new LogoutResponse(true);
    }
}
