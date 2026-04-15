package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.LogoutResponse;
import com.springboot.eduko.dtos.TokenBlackListDto;
import com.springboot.eduko.jwt.HandleToken;
import com.springboot.eduko.mapper.TokenBlackListMapper;
import com.springboot.eduko.model.TokenBlackList;
import com.springboot.eduko.repo.TokenBlackListRepo;
import com.springboot.eduko.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenBlackListServiceImpl  implements TokenBlackListService {
    private final TokenBlackListRepo tokenBlackListRepo;
    private final TokenBlackListMapper tokenBlackListMapper;

    TokenBlackListServiceImpl(TokenBlackListMapper tokenBlackListMapper,TokenBlackListRepo tokenBlackListRepo){
        this.tokenBlackListMapper=tokenBlackListMapper;
        this.tokenBlackListRepo=tokenBlackListRepo;
    }
    @Override
    public void saveTokenBlackList(String token, Date expireDate) {
        TokenBlackListDto tokenBlackListDto=new TokenBlackListDto();
        tokenBlackListDto.setToken(token);
        tokenBlackListDto.setExpireDate(expireDate);
        TokenBlackList tokenBlackList=tokenBlackListMapper.toEntity(tokenBlackListDto);
        tokenBlackListRepo.save(tokenBlackList);
    }

    @Override
    public boolean isBlackList(String token) {
        return tokenBlackListRepo.existsByToken(token);
    }


}
