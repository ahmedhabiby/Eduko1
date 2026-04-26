package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.EnrollmentDto;
import com.springboot.eduko.model.Enrollments;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    Enrollments toEntity(EnrollmentDto dto);
    EnrollmentDto toDto(Enrollments entity);
    List<EnrollmentDto> toDto(List<Enrollments> entity);
    List<Enrollments> toEntity(List<EnrollmentDto> dto);
}
