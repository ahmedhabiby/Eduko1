package com.springboot.eduko.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Enrollments extends BaseEntity {

    @ManyToOne
    private Student    student;

    @ManyToOne
    private EduCourses eduCourses;

    // ── Base status ──────────────────────────────────────────────────────
    /** "active" | "completed" | "dropped" | "expired" */
    private String status;

    // ── Access control ───────────────────────────────────────────────────
    /** "full" | "module" | "lesson" — granularity of access granted */
    private String accessLevel = "full";

    /** ISO-8601 when access was granted by admin. */
    private String accessGrantedAt;

    /** ISO-8601 expiry date; null = no expiry. */
    private String accessExpiresAt;

    // ── Payment tracking (manual proof workflow) ─────────────────────────
    /**
     * Payment status in the manual-proof workflow.
     * Values: "free" | "pending_proof" | "proof_submitted" | "approved" | "rejected"
     */
    private String paymentStatus = "free";

    /** URL of the uploaded payment receipt image. */
    private String paymentProofUrl;

    /** Amount paid in the course currency. */
    private Double paymentAmount;

    /** Admin user ID who approved/rejected the payment. */
    private String paymentApprovedBy;

    /** ISO-8601 when admin reviewed the payment. */
    private String paymentApprovedAt;

    // ── Completion ───────────────────────────────────────────────────────
    /** ISO-8601 when student completed the course. */
    private String completedAt;
}
