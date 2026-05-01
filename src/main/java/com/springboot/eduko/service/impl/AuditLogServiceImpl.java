package com.springboot.eduko.service.impl;

import com.springboot.eduko.model.AuditLog;
import com.springboot.eduko.repo.AuditLogRepo;
import com.springboot.eduko.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepo auditLogRepo;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepo auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    @Override
    public void log(String actorName, String action, String target) {
        AuditLog entry = new AuditLog();
        entry.setActorName(actorName);
        entry.setAction(action);
        entry.setTarget(target);
        auditLogRepo.save(entry);
    }
}
