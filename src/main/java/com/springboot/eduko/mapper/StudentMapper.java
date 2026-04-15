package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.model.Student;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    Student toEntity(StudentDto dto);
    StudentDto toDto(Student entity);
    List<StudentDto> toDto(List<Student> entity);
    List<Student> toEntity(List<StudentDto> dto);
}
