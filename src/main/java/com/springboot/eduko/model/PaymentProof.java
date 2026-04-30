package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a manual payment proof submission from a Student.
 *
 * Workflow:
 *   1. Student submits proof (POST /proofs) — status = "pending"
 *   2. Enrollment.paymentStatus → "proof_submitted"
 *   3. Admin reviews and either:
 *      POST /payments/approvals/{id}/approve → status = "approved"
 *      POST /payments/approvals/{id}/reject  → status = "rejected"
 *   4. On approval: Enrollment.paymentStatus → "approved",
 *      Enrollment.accessGrantedAt is set.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProof extends BaseEntity {

    @ManyToOne
    private Student student;

    @ManyToOne
    private EduCourses course;

    /** Public URL of the uploaded receipt image. */
    private String proofUrl;

    private Double  amount;
    private String  currency;      // "EGP" | "USD"

    /**
     * Review status.
     * Values: "pending" | "approved" | "rejected"
     */
    private String  status = "pending";

    /** BaseUser ID (string) of the admin who reviewed. */
    private String  reviewedBy;

    /** ISO-8601 timestamp of admin review. */
    private String  reviewedAt;

    /** Reason provided by admin when rejecting. */
    private String  rejectReason;

    // ── Denormalised for quick admin list view ───────────────────────────
    private String studentName;
    private String courseName;
}
