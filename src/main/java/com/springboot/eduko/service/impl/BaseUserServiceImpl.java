package com.springboot.eduko.service.impl;

import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.mapper.BaseUserMapper;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.service.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseUserServiceImpl implements BaseUserService {
    private BaseUserRepo baseUserRepo;
    private BaseUserMapper baseUserMapper;


    @Autowired
    public BaseUserServiceImpl(BaseUserRepo baseUserRepo,BaseUserMapper baseUserMapper){
        this.baseUserMapper=baseUserMapper;
        this.baseUserRepo=baseUserRepo;
    }

    @Override
    public BaseUserDto getUserByEmail(String email) {
        BaseUser baseUser=baseUserRepo.findByEmailWithRoles(email).orElseThrow();
        return baseUserMapper.toDto(baseUser);
    }
}
