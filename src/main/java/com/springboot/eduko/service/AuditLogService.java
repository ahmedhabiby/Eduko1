package com.springboot.eduko.service;

public interface AuditLogService {

    /**
     * Record an admin action in the audit log.
     *
     * @param actorName human-readable name of the admin performing the action
     * @param action    machine-readable key, e.g. "approved_payment", "banned_user"
     * @param target    human-readable description of the affected entity,
     *                  e.g. "Student: Ahmed Hassan (ID 42)"
     */
    void log(String actorName, String action, String target);
}
