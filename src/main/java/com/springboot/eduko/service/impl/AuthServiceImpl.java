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
import java.util.Map;
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
    public AuthServiceImpl(
            HandleToken handleToken,
            HttpServletRequest httpServletRequest,
            TeacherMapper teacherMapper,
            TokenBlackListService tokenBlackListService,
            TeacherService teacherService,
            StudentMapper studentMapper,
            RoleRepo roleRepo,
            PasswordEncoder passwordEncoder,
            BaseUserMapper baseUserMapper,
            BaseUserService baseUserService,
            BaseUserRepo baseUserRepo,
            StudentService studentService) {
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.handleToken = handleToken;
        this.baseUserMapper = baseUserMapper;
        this.baseUserRepo = baseUserRepo;
        this.baseUserService = baseUserService;
        this.studentService = studentService;
        this.studentMapper = studentMapper;
        this.teacherService = teacherService;
        this.teacherMapper = teacherMapper;
        this.tokenBlackListService = tokenBlackListService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        BaseUserDto baseUserDto = baseUserService.getUserByEmail(loginRequest.getEmail());
        if (Objects.isNull(baseUserDto))
            throw new RuntimeException("email.not.exist");
        if (!passwordEncoder.matches(loginRequest.getPassword(),
                baseUserDto.getPassword().replace("{bcrypt}", "")))
            throw new RuntimeException("Invalid.password");

        String name  = "";
        String role  = "student";
        Long   userId = baseUserDto.getId();

        if (baseUserDto.getStudent() != null && baseUserDto.getStudent().getId() != null) {
            name = studentService.getStudentNameById(baseUserDto.getStudent().getId());
            role = "student";
        } else if (baseUserDto.getTeacher() != null && baseUserDto.getTeacher().getId() != null) {
            name = teacherService.getTeacherNameById(baseUserDto.getTeacher().getId());
            role = "teacher";
        }

        String token = handleToken.generateToken(baseUserDto);
        AuthResponse.UserPayload payload =
                new AuthResponse.UserPayload(userId, name, baseUserDto.getEmail(), role, null);
        return new AuthResponse(token, payload);
    }

    @Override
    public AuthResponse signupForStudent(SignupRequestForStudent signupRequestForStudent) {
        BaseUserDto baseUserDto = new BaseUserDto();
        BaseUser existingUser = baseUserRepo.findBaseUsersByEmail(signupRequestForStudent.getEmail());
        if (Objects.nonNull(existingUser))
            throw new RuntimeException("Student.already.exist");

        baseUserDto.setEmail(signupRequestForStudent.getEmail());
        baseUserDto.setPassword(passwordEncoder.encode(signupRequestForStudent.getPassword()));

        EduRoles roles = roleRepo.findByRoleId(3L);
        if (Objects.isNull(roles))
            throw new RuntimeException("Role.not.found");

        BaseUser baseUser1 = baseUserMapper.toEntity(baseUserDto);
        baseUser1.setRoles(List.of(roles));

        // parentName, parentNumber, studentNumber are optional — may be null
        StudentData studentData = new StudentData(
                signupRequestForStudent.getFirstName(),
                signupRequestForStudent.getLastName(),
                signupRequestForStudent.getParentName(),
                signupRequestForStudent.getParentNumber(),
                signupRequestForStudent.getStudentNumber()
        );
        StudentDto studentDto = studentService.saveStudent(studentData);
        baseUser1.setStudent(studentMapper.toEntity(studentDto));
        BaseUser savedUser = baseUserRepo.save(baseUser1);
        BaseUserDto savedDto = baseUserMapper.toDto(savedUser);

        String token = handleToken.generateToken(savedDto);
        AuthResponse.UserPayload payload = new AuthResponse.UserPayload(
                savedDto.getId(),
                savedDto.getStudent().getFirstName(),
                savedDto.getEmail(),
                "student",
                null
        );
        return new AuthResponse(token, payload);
    }

    @Override
    public AuthResponse signupForTeacher(SignupRequestForTeachers signupRequestForTeachers) {
        BaseUserDto baseUserDto = new BaseUserDto();
        BaseUser existingUser = baseUserRepo.findBaseUsersByEmail(signupRequestForTeachers.getEmail());
        if (Objects.nonNull(existingUser))
            throw new RuntimeException("teacher.already.exist");

        baseUserDto.setEmail(signupRequestForTeachers.getEmail());
        baseUserDto.setPassword(passwordEncoder.encode(signupRequestForTeachers.getPassword()));

        EduRoles roles = roleRepo.findByRoleId(2L);
        if (Objects.isNull(roles))
            throw new RuntimeException("Role.not.found");

        BaseUser baseUser1 = baseUserMapper.toEntity(baseUserDto);
        baseUser1.setRoles(List.of(roles));

        TeacherData teacherData = new TeacherData(signupRequestForTeachers.getTeacherName());
        TeacherDto teacherDto = teacherService.saveTeacher(teacherData);
        baseUser1.setTeacher(teacherMapper.toEntity(teacherDto));
        BaseUser savedUser = baseUserRepo.save(baseUser1);
        BaseUserDto savedDto = baseUserMapper.toDto(savedUser);

        String token = handleToken.generateToken(savedDto);
        AuthResponse.UserPayload payload = new AuthResponse.UserPayload(
                savedDto.getId(),
                savedDto.getTeacher().getTeacherName(),
                savedDto.getEmail(),
                "teacher",
                null
        );
        return new AuthResponse(token, payload);
    }

    @Override
    public LogoutResponse logout() {
        String header = httpServletRequest.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Date expireDate = handleToken.getExpireDateFromToken(token);
            tokenBlackListService.saveTokenBlackList(token, expireDate);
        }
        return new LogoutResponse(true);
    }

    @Override
    public ResetPassword changePassword(RequestForNewPass requestForNewPass) {
        BaseUser baseUser = baseUserRepo.findBaseUsersByEmail(requestForNewPass.getEmail());
        if (Objects.isNull(baseUser))
            throw new RuntimeException("user.not.found");
        String token = handleToken.generateTokenForResetPassword(baseUserMapper.toDto(baseUser));
        BaseUserDto baseUserDto = handleToken.validateToken(token);
        if (Objects.isNull(baseUserDto))
            throw new RuntimeException("user.not.found");
        baseUser.setPassword(passwordEncoder.encode(requestForNewPass.getNewPassword()));
        baseUserRepo.save(baseUser);
        return new ResetPassword("password changed successfully");
    }

    @Override
    public Object forgotPassword(ForgotPasswordRequest request) {
        BaseUser baseUser = baseUserRepo.findBaseUsersByEmail(request.getEmail());
        if (Objects.isNull(baseUser))
            throw new RuntimeException("user.not.found");
        String resetToken = handleToken.generateTokenForResetPassword(baseUserMapper.toDto(baseUser));
        // TODO: In production, send resetToken via email instead of returning it
        return Map.of("resetToken", resetToken);
    }

    @Override
    public ResetPassword resetPassword(ResetPasswordRequest request) {
        BaseUserDto baseUserDto = handleToken.validateToken(request.getToken());
        if (Objects.isNull(baseUserDto))
            throw new RuntimeException("invalid.or.expired.token");
        BaseUser baseUser = baseUserRepo.findBaseUsersByEmail(baseUserDto.getEmail());
        if (Objects.isNull(baseUser))
            throw new RuntimeException("user.not.found");
        baseUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        baseUserRepo.save(baseUser);
        return new ResetPassword("password changed successfully");
    }
}
