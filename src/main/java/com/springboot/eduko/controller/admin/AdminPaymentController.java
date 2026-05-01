package com.springboot.eduko.controller.admin;

import com.springboot.eduko.model.Enrollments;
import com.springboot.eduko.model.PaymentProof;
import com.springboot.eduko.repo.EnrollmentRepo;
import com.springboot.eduko.repo.PaymentProofRepo;
import com.springboot.eduko.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Admin — Payments", description = "View payments and manage manual proof approvals")
@RestController
@RequestMapping("/admin")
public class AdminPaymentController {

    private final PaymentProofRepo paymentProofRepo;
    private final EnrollmentRepo   enrollmentRepo;
    private final AuditLogService  auditLogService;

    @Autowired
    public AdminPaymentController(PaymentProofRepo paymentProofRepo,
                                  EnrollmentRepo enrollmentRepo,
                                  AuditLogService auditLogService) {
        this.paymentProofRepo = paymentProofRepo;
        this.enrollmentRepo   = enrollmentRepo;
        this.auditLogService  = auditLogService;
    }

    private String actor() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Map<String, Object> toProofMap(PaymentProof p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",           p.getId().toString());
        m.put("proofUrl",     p.getProofUrl());
        m.put("amount",       p.getAmount());
        m.put("currency",     p.getCurrency());
        m.put("status",       p.getStatus());
        m.put("reviewedBy",   p.getReviewedBy());
        m.put("reviewedAt",   p.getReviewedAt());
        m.put("rejectReason", p.getRejectReason());
        m.put("studentName",  p.getStudentName());
        m.put("courseName",   p.getCourseName());
        m.put("createdAt",    p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
        if (p.getStudent() != null) m.put("studentId", p.getStudent().getId().toString());
        if (p.getCourse()  != null) m.put("courseId",  p.getCourse().getId().toString());
        return m;
    }

    // ═════════════════════ /admin/payments ══════════════════════

    @Operation(summary = "List all payments (approved proofs + enrollment payment info)")
    @GetMapping("/payments")
    public ResponseEntity<Map<String, Object>> listPayments() {
        var payments = paymentProofRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(p -> "approved".equals(p.getStatus()))
                .map(this::toProofMap).toList();
        return ResponseEntity.ok(Map.of("payments", payments));
    }

    // ═════════════════════ /admin/payments/approvals ═══════════════

    @Operation(summary = "List pending payment proofs awaiting admin review")
    @GetMapping("/payments/approvals")
    public ResponseEntity<Map<String, Object>> listApprovals() {
        var pending = paymentProofRepo.findByStatus("pending")
                .stream().map(this::toProofMap).toList();
        return ResponseEntity.ok(Map.of("proofs", pending));
    }

    @Operation(summary = "Approve a payment proof",
               description = "Sets proof status=approved and updates corresponding enrollment payment status.")
    @PostMapping("/payments/approvals/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveProof(@PathVariable Long id) {
        return paymentProofRepo.findById(id).map(proof -> {
            proof.setStatus("approved");
            proof.setReviewedBy(actor());
            proof.setReviewedAt(LocalDateTime.now().toString());
            paymentProofRepo.save(proof);

            // Sync enrollment
            if (proof.getStudent() != null && proof.getCourse() != null) {
                enrollmentRepo
                        .findByStudentIdAndEduCoursesId(
                                proof.getStudent().getId(),
                                proof.getCourse().getId())
                        .ifPresent(e -> {
                            e.setPaymentStatus("approved");
                            e.setPaymentApprovedBy(actor());
                            e.setPaymentApprovedAt(LocalDateTime.now().toString());
                            e.setAccessGrantedAt(LocalDateTime.now().toString());
                            e.setStatus("active");
                            enrollmentRepo.save(e);
                        });
            }

            auditLogService.log(actor(), "approved_payment", "Proof ID: " + id);
            return ResponseEntity.ok(Map.of("proof", toProofMap(proof)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Reject a payment proof")
    @PostMapping("/payments/approvals/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectProof(@PathVariable Long id,
                                                           @RequestBody(required = false) Map<String, String> body) {
        return paymentProofRepo.findById(id).map(proof -> {
            proof.setStatus("rejected");
            proof.setReviewedBy(actor());
            proof.setReviewedAt(LocalDateTime.now().toString());
            if (body != null && body.containsKey("reason"))
                proof.setRejectReason(body.get("reason"));
            paymentProofRepo.save(proof);

            // Sync enrollment
            if (proof.getStudent() != null && proof.getCourse() != null) {
                enrollmentRepo
                        .findByStudentIdAndEduCoursesId(
                                proof.getStudent().getId(),
                                proof.getCourse().getId())
                        .ifPresent(e -> {
                            e.setPaymentStatus("rejected");
                            enrollmentRepo.save(e);
                        });
            }

            auditLogService.log(actor(), "rejected_payment", "Proof ID: " + id);
            return ResponseEntity.ok(Map.of("proof", toProofMap(proof)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ═════════════════════ /admin/proofs ═══════════════════─

    @Operation(summary = "List all payment proofs (all statuses)")
    @GetMapping("/proofs")
    public ResponseEntity<Map<String, Object>> listAllProofs() {
        var proofs = paymentProofRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toProofMap).toList();
        return ResponseEntity.ok(Map.of("proofs", proofs));
    }

    @Operation(summary = "Student submits payment proof",
               description = "Used by student-facing API. Creates a PaymentProof with status=pending.")
    @PostMapping("/proofs")
    public ResponseEntity<Map<String, Object>> submitProof(@RequestBody Map<String, Object> body) {
        PaymentProof proof = new PaymentProof();
        proof.setProofUrl((String) body.get("proofUrl"));
        proof.setAmount(body.get("amount") != null ? ((Number) body.get("amount")).doubleValue() : null);
        proof.setCurrency((String) body.getOrDefault("currency", "EGP"));
        proof.setStatus("pending");
        proof.setStudentName((String) body.getOrDefault("studentName", ""));
        proof.setCourseName((String)  body.getOrDefault("courseName", ""));
        paymentProofRepo.save(proof);
        return ResponseEntity.ok(Map.of("proof", toProofMap(proof)));
    }
}
