package com.springboot.eduko.mapper;

import com.springboot.eduko.dtos.TokenBlackListDto;
import com.springboot.eduko.model.TokenBlackList;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TokenBlackListMapper {
    TokenBlackList toEntity(TokenBlackListDto tokenBlackListDto);
    TokenBlackListDto toDto(TokenBlackList entity);
    List<TokenBlackListDto> toDto(List<TokenBlackList> entity);
    List<TokenBlackList> toEntity(List<TokenBlackListDto> dto);
}
