package com.springboot.eduko.service;

import com.springboot.eduko.dtos.BaseUserDto;

public interface BaseUserService {
    BaseUserDto getUserByEmail(String email);

}
