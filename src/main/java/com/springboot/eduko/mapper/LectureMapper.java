package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.LectureDto;
import com.springboot.eduko.model.Lectures;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LectureMapper {
    Lectures toEntity(LectureDto dto);
    LectureDto toDto(Lectures entity);
    List<LectureDto> toDto(List<Lectures> entity);
    List<Lectures> toEntity(List<LectureDto> dto);
}
