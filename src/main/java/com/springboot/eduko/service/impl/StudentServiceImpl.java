package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.AuthStudentResponse;
import com.springboot.eduko.controller.vms.StudentData;
import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.mapper.StudentMapper;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.StudentRepo;
import com.springboot.eduko.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StudentServiceImpl implements StudentService {
    private StudentRepo studentRepo;
    private StudentMapper studentMapper;
    private BaseUserRepo baseUserRepo;

    @Autowired
    public StudentServiceImpl(StudentRepo studentRepo, StudentMapper studentMapper,BaseUserRepo baseUserRepo) {
        this.studentRepo = studentRepo;
        this.studentMapper = studentMapper;
        this.baseUserRepo=baseUserRepo;
    }

    @Override
    public StudentDto saveStudent(StudentData studentData) {
        StudentDto studentDto=new StudentDto();
        studentDto.setStudentNumber(studentData.getStudentNumber());
        studentDto.setFirstName(studentData.getFirstName());
        studentDto.setLastName(studentData.getLastName());
        studentDto.setParentName(studentData.getParentName());
        studentDto.setParentNumber(studentData.getParentNumber());
        Student student = studentMapper.toEntity(studentDto);
        return studentMapper.toDto(studentRepo.save(student));
    }

    @Override
    public String getStudentNameById(Long id) {
        Student student =studentRepo.getStudentById(id);
        if (Objects.isNull(student))
            throw new RuntimeException("Student.not.found");
        return studentMapper.toDto(student).getFirstName();
    }

    @Override
    public AuthStudentResponse getAuthStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        BaseUser baseUser = baseUserRepo.findBaseUsersByEmail(email);

        if (baseUser == null) {
            throw new RuntimeException("user.not.found");
        }

        if (baseUser.getStudent() == null) {
            throw new RuntimeException("student.not.found");
        }

        Student student = studentRepo.findById(baseUser.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("student.not.found"));

        return new AuthStudentResponse(
                student.getFirstName(),
                student.getLastName(),
                baseUser.getEmail(),
                student.getParentName(),
                student.getParentNumber(),
                student.getStudentNumber()
        );
    }
}
