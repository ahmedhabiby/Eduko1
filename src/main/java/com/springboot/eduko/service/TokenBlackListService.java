package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.LogoutResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Date;

public interface TokenBlackListService {
    void saveTokenBlackList(String token , Date expireDate);
    boolean isBlackList(String token);
}
