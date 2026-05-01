package com.springboot.eduko.controller.admin;

import com.springboot.eduko.model.AuditLog;
import com.springboot.eduko.repo.AuditLogRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin — Audit Logs", description = "Immutable audit trail of all admin actions")
@RestController
@RequestMapping("/admin/audit-logs")
public class AdminAuditController {

    private final AuditLogRepo auditLogRepo;

    @Autowired
    public AdminAuditController(AuditLogRepo auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    @Operation(summary = "List audit logs",
               description = "Returns the most recent admin actions. Use ?page=0&size=50 for pagination.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size) {

        List<Map<String, Object>> logs = auditLogRepo
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .stream()
                .map(entry -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",        entry.getId().toString());
                    m.put("actorName", entry.getActorName());
                    m.put("action",    entry.getAction());
                    m.put("target",    entry.getTarget());
                    m.put("createdAt", entry.getCreatedAt() != null
                            ? entry.getCreatedAt().toString() : null);
                    return m;
                }).toList();

        return ResponseEntity.ok(Map.of(
                "logs",  logs,
                "page",  page,
                "size",  size
        ));
    }
}
