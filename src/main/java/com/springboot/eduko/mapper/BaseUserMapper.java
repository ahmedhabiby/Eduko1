package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.Student;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseUserMapper {
    BaseUser toEntity(BaseUserDto dto);
    BaseUserDto toDto(BaseUser entity);
    List<BaseUserDto> toDto(List<BaseUser> entity);
    List<BaseUser> toEntity(List<BaseUserDto> dto);
}
