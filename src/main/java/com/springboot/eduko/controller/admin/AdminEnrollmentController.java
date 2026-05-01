package com.springboot.eduko.controller.admin;

import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Enrollments;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.EnrollmentRepo;
import com.springboot.eduko.repo.StudentRepo;
import com.springboot.eduko.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin — Enrollments", description = "Manage student enrollments, approvals, and access control")
@RestController
@RequestMapping("/admin/enrollments")
public class AdminEnrollmentController {

    private final EnrollmentRepo  enrollmentRepo;
    private final CourseRepo      courseRepo;
    private final StudentRepo     studentRepo;
    private final AuditLogService auditLogService;

    @Autowired
    public AdminEnrollmentController(EnrollmentRepo enrollmentRepo,
                                     CourseRepo courseRepo,
                                     StudentRepo studentRepo,
                                     AuditLogService auditLogService) {
        this.enrollmentRepo  = enrollmentRepo;
        this.courseRepo      = courseRepo;
        this.studentRepo     = studentRepo;
        this.auditLogService = auditLogService;
    }

    private String actor() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Map<String, Object> toMap(Enrollments e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",                e.getId().toString());
        m.put("status",            e.getStatus());
        m.put("accessLevel",       e.getAccessLevel());
        m.put("paymentStatus",     e.getPaymentStatus());
        m.put("paymentAmount",     e.getPaymentAmount());
        m.put("paymentProofUrl",   e.getPaymentProofUrl());
        m.put("paymentApprovedBy", e.getPaymentApprovedBy());
        m.put("paymentApprovedAt", e.getPaymentApprovedAt());
        m.put("accessGrantedAt",   e.getAccessGrantedAt());
        m.put("accessExpiresAt",   e.getAccessExpiresAt());
        m.put("completedAt",       e.getCompletedAt());
        m.put("createdAt",         e.getCreatedAt() != null ? e.getCreatedAt().toString() : null);
        m.put("progressPercent",   e.getProgressPercent());

        if (e.getStudent() != null) {
            Student s = e.getStudent();
            m.put("studentId",   s.getId().toString());
            m.put("studentName", (s.getFirstName() != null ? s.getFirstName() : "") + " " +
                                 (s.getLastName()  != null ? s.getLastName()  : ""));
        }
        if (e.getEduCourses() != null) {
            EduCourses c = e.getEduCourses();
            m.put("courseId",    c.getId().toString());
            m.put("courseName",  c.getCourseTitle());
        }
        return m;
    }

    // ══════════════════════ CRUD ══════════════════════

    @Operation(summary = "List all enrollments")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        var list = enrollmentRepo.findAll().stream().map(this::toMap).toList();
        return ResponseEntity.ok(Map.of("enrollments", list));
    }

    @Operation(summary = "Create enrollment (manual admin enroll)")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Long studentId = Long.parseLong(body.get("studentId").toString());
        Long courseId  = Long.parseLong(body.get("courseId").toString());

        Student    student = studentRepo.findById(studentId).orElse(null);
        EduCourses course  = courseRepo.findById(courseId).orElse(null);
        if (student == null || course == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Student or Course not found"));

        Enrollments e = new Enrollments();
        e.setStudent(student);
        e.setEduCourses(course);
        e.setStatus("active");
        e.setPaymentStatus(course.getPrice() != null && course.getPrice() > 0 ? "pending_proof" : "free");
        e.setAccessGrantedAt(LocalDateTime.now().toString());
        e.setProgressPercent(0);
        enrollmentRepo.save(e);
        auditLogService.log(actor(), "enrolled_student",
                "Student ID: " + studentId + " in Course ID: " + courseId);
        return ResponseEntity.ok(Map.of("enrollment", toMap(e)));
    }

    @Operation(summary = "Get enrollment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        return enrollmentRepo.findById(id)
                .map(e -> ResponseEntity.ok(Map.of("enrollment", toMap(e))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete enrollment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        if (!enrollmentRepo.existsById(id)) return ResponseEntity.notFound().build();
        enrollmentRepo.deleteById(id);
        auditLogService.log(actor(), "deleted_enrollment", "Enrollment ID: " + id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ══════════════════════ WORKFLOW ══════════════════════

    @Operation(summary = "Approve enrollment",
               description = "Sets paymentStatus=approved, status=active, grants access.")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id) {
        return enrollmentRepo.findById(id).map(e -> {
            e.setPaymentStatus("approved");
            e.setStatus("active");
            e.setPaymentApprovedBy(actor());
            e.setPaymentApprovedAt(LocalDateTime.now().toString());
            e.setAccessGrantedAt(LocalDateTime.now().toString());
            enrollmentRepo.save(e);
            auditLogService.log(actor(), "approved_enrollment", "Enrollment ID: " + id);
            return ResponseEntity.ok(Map.of("enrollment", toMap(e)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Reject enrollment",
               description = "Sets paymentStatus=rejected, status=dropped.")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> reject(@PathVariable Long id,
                                                      @RequestBody(required = false) Map<String, String> body) {
        return enrollmentRepo.findById(id).map(e -> {
            e.setPaymentStatus("rejected");
            e.setStatus("dropped");
            e.setPaymentApprovedBy(actor());
            e.setPaymentApprovedAt(LocalDateTime.now().toString());
            enrollmentRepo.save(e);
            auditLogService.log(actor(), "rejected_enrollment", "Enrollment ID: " + id);
            return ResponseEntity.ok(Map.of("enrollment", toMap(e)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update access level",
               description = "Change accessLevel to full | module | lesson.")
    @PatchMapping("/{id}/access")
    public ResponseEntity<Map<String, Object>> updateAccess(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        return enrollmentRepo.findById(id).map(e -> {
            if (body.containsKey("accessLevel"))     e.setAccessLevel(body.get("accessLevel"));
            if (body.containsKey("accessExpiresAt")) e.setAccessExpiresAt(body.get("accessExpiresAt"));
            enrollmentRepo.save(e);
            auditLogService.log(actor(), "updated_access", "Enrollment ID: " + id);
            return ResponseEntity.ok(Map.of("enrollment", toMap(e)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ──────────────────────────────────────────────────────────────────
    @Operation(summary = "Update lesson progress (admin override)",
               description = "Marks a specific lesson as done/undone and recalculates progressPercent. " +
                             "Body: { lessonId: string, done: boolean }")
    @PatchMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(@PathVariable Long id,
                                                              @RequestBody Map<String, Object> body) {
        return enrollmentRepo.findById(id).map(e -> {
            String lessonId = (String) body.get("lessonId");
            boolean done    = Boolean.TRUE.equals(body.get("done"));

            if (lessonId == null || lessonId.isBlank())
                return ResponseEntity.badRequest()
                        .<Map<String, Object>>body(Map.of("message", "lessonId is required"));

            // Use existing lessonProgress map (stored as JSON in the column)
            Map<String, Boolean> progress = e.getLessonProgress();
            if (progress == null) progress = new HashMap<>();
            progress.put(lessonId, done);
            e.setLessonProgress(progress);

            // Recalculate percent
            int total    = progress.size();
            int completed = (int) progress.values().stream().filter(Boolean::booleanValue).count();
            int percent  = total > 0 ? Math.round((float) completed / total * 100) : 0;
            e.setProgressPercent(percent);

            // Auto-complete enrollment if 100%
            if (percent == 100 && !"completed".equals(e.getStatus())) {
                e.setStatus("completed");
                e.setCompletedAt(LocalDateTime.now().toString());
            }

            enrollmentRepo.save(e);
            auditLogService.log(actor(), "updated_progress",
                    "Enrollment ID: " + id + " lesson=" + lessonId + " done=" + done);

            return ResponseEntity.ok(Map.<String, Object>of(
                    "progressPercent", percent,
                    "lessonId", lessonId,
                    "done", done
            ));
        }).orElse(ResponseEntity.notFound().build());
    }
}
