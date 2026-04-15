package com.springboot.eduko.service.impl;

import com.springboot.eduko.repo.TokenBlackListRepo;
import com.springboot.eduko.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private TokenBlackListRepo tokenBlackListRepo;

    @Autowired
    public ScheduleServiceImpl(TokenBlackListRepo tokenBlackListRepo) {
        this.tokenBlackListRepo = tokenBlackListRepo;
    }

    @Override
    @Scheduled(fixedDelay =86400000)
    public void deleteAllInTokenBlackListTable() {
        tokenBlackListRepo.deleteAll();
    }
}
