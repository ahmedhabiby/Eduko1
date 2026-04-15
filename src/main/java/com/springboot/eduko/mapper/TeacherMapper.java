package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.dtos.TeacherDto;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.model.Teacher;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    Teacher toEntity(TeacherDto dto);
    TeacherDto toDto(Teacher entity);
    List<TeacherDto> toDto(List<Teacher> entity);
    List<Teacher> toEntity(List<TeacherDto> dto);
}
