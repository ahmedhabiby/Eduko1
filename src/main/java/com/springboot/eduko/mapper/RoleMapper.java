package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.RoleDto;
import com.springboot.eduko.model.EduRoles;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    EduRoles toEntity(RoleDto dto);
    RoleDto toDto(EduRoles entity);
    List<RoleDto> toDto(List<EduRoles> entity);
    List<EduRoles> toEntity(List<RoleDto> dto);
}
