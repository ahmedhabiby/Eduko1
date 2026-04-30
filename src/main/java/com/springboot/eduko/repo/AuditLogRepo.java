package com.springboot.eduko.repo;

import com.springboot.eduko.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {

    /** Latest audit logs, most-recent first. */
    List<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
