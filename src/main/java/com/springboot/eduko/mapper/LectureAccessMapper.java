package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.LectureAccessDto;
import com.springboot.eduko.model.LectureAccess;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LectureAccessMapper {
    LectureAccess toEntity(LectureAccessDto dto);
    LectureAccessDto toDto(LectureAccess entity);
    List<LectureAccessDto> toDto(List<LectureAccess> entity);
    List<LectureAccess> toEntity(List<LectureAccessDto> dto);
}
