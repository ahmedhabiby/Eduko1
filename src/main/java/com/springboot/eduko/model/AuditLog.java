package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Immutable audit trail for all Admin panel actions.
 *
 * Created automatically by AuditLogService.log() after
 * every create / update / delete / approve / reject operation.
 *
 * Examples of action values:
 *   created_user, deleted_user, changed_status, banned_user,
 *   created_course, enrolled_student, approved_payment,
 *   rejected_payment, reset_password
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog extends BaseEntity {

    /** Name of the admin (or teacher) who performed the action. */
    private String actorName;

    /** Machine-readable action key, e.g. "approved_payment". */
    private String action;

    /** Human-readable description of what was affected. */
    private String target;
}
