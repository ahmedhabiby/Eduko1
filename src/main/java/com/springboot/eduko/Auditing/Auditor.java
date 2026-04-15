package com.springboot.eduko.Auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("audit")
public class Auditor implements AuditorAware {
    @Override
    public Optional getCurrentAuditor() {
        return Optional.of("admin");
    }
}
