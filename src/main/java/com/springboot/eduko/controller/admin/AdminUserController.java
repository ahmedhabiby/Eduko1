package com.springboot.eduko.controller.admin;

import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.EduRoles;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.EnrollmentRepo;
import com.springboot.eduko.repo.RoleRepo;
import com.springboot.eduko.repo.StudentRepo;
import com.springboot.eduko.repo.TeacherRepo;
import com.springboot.eduko.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Admin — Users", description = "Manage students, teachers, and all platform users")
@RestController
@RequestMapping("/admin")
public class AdminUserController {

    private final BaseUserRepo    baseUserRepo;
    private final StudentRepo     studentRepo;
    private final TeacherRepo     teacherRepo;
    private final RoleRepo        roleRepo;
    private final EnrollmentRepo  enrollmentRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Autowired
    public AdminUserController(BaseUserRepo baseUserRepo,
                               StudentRepo studentRepo,
                               TeacherRepo teacherRepo,
                               RoleRepo roleRepo,
                               EnrollmentRepo enrollmentRepo,
                               PasswordEncoder passwordEncoder,
                               AuditLogService auditLogService) {
        this.baseUserRepo    = baseUserRepo;
        this.studentRepo     = studentRepo;
        this.teacherRepo     = teacherRepo;
        this.roleRepo        = roleRepo;
        this.enrollmentRepo  = enrollmentRepo;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    // ══════════════════════ HELPERS ════════════════════════

    private String actorName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String resolveRole(BaseUser u) {
        if (u.getRoles() == null) return "student";
        for (EduRoles r : u.getRoles()) {
            if ("ADMIN".equalsIgnoreCase(r.getRole()))   return "admin";
            if ("TEACHER".equalsIgnoreCase(r.getRole())) return "teacher";
        }
        return "student";
    }

    private Map<String, Object> toUserMap(BaseUser u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",          u.getId().toString());
        m.put("email",       u.getEmail());
        m.put("status",      u.getStatus() != null ? u.getStatus() : "active");
        m.put("lastLoginAt", u.getLastLoginAt());
        m.put("createdAt",   u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
        m.put("role",        resolveRole(u));

        if (u.getStudent() != null) {
            var s = u.getStudent();
            String name = (s.getFirstName() != null ? s.getFirstName() : "") + " " +
                          (s.getLastName()  != null ? s.getLastName()  : "");
            m.put("nameEn", name.trim());
            m.put("nameAr", name.trim());
            m.put("studentNumber", s.getStudentNumber());
            m.put("parentName",   s.getParentName());
            m.put("parentNumber", s.getParentNumber());
            var enrollments = enrollmentRepo.findByStudentId(s.getId());
            m.put("enrolledCount",   enrollments.size());
            m.put("completedCount",  enrollments.stream()
                    .filter(e -> "completed".equals(e.getStatus())).count());
        }
        if (u.getTeacher() != null) {
            var t = u.getTeacher();
            m.put("nameEn",       t.getTeacherName());
            m.put("nameAr",       t.getTeacherName());
            var courses = t.getEduCoursesList();
            m.put("coursesCount", courses != null ? courses.size() : 0);
            int enrolled = (courses != null)
                    ? courses.stream()
                             .mapToInt(c -> c.getEnrollments() != null ? c.getEnrollments().size() : 0)
                             .sum()
                    : 0;
            m.put("studentsCount", enrolled);
        }
        return m;
    }

    // ══════════════════════ /admin/users ════════════════════════

    @Operation(summary = "List all users")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> listUsers() {
        var users = baseUserRepo.findAll().stream().map(this::toUserMap).toList();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @Operation(summary = "Get current admin profile")
    @GetMapping("/users/me")
    public ResponseEntity<Map<String, Object>> me() {
        String email = actorName();
        BaseUser u = baseUserRepo.findBaseUsersByEmail(email);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("user", toUserMap(u)));
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        return baseUserRepo.findById(id)
                .map(u -> ResponseEntity.ok(Map.of("user", toUserMap(u))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user")
    @PatchMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> body) {
        return baseUserRepo.findById(id).map(u -> {
            if (body.containsKey("email"))  u.setEmail((String) body.get("email"));
            if (body.containsKey("status")) u.setStatus((String) body.get("status"));
            baseUserRepo.save(u);
            auditLogService.log(actorName(), "updated_user", "User ID: " + id);
            return ResponseEntity.ok(Map.of("user", toUserMap(u)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        if (!baseUserRepo.existsById(id)) return ResponseEntity.notFound().build();
        baseUserRepo.deleteById(id);
        auditLogService.log(actorName(), "deleted_user", "User ID: " + id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @Operation(summary = "Update user status (active/inactive/banned)")
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        return baseUserRepo.findById(id).map(u -> {
            String newStatus = body.get("status");
            u.setStatus(newStatus);
            baseUserRepo.save(u);
            auditLogService.log(actorName(), "changed_status",
                    "User ID: " + id + " → " + newStatus);
            return ResponseEntity.ok(Map.of("user", toUserMap(u)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Reset user password", description = "Generates a random temporary password.")
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id) {
        return baseUserRepo.findById(id).map(u -> {
            String tempPass = "Eduko@" + (int)(Math.random() * 90000 + 10000);
            u.setPassword(passwordEncoder.encode(tempPass));
            baseUserRepo.save(u);
            auditLogService.log(actorName(), "reset_password", "User ID: " + id);
            return ResponseEntity.ok(Map.of("tempPassword", tempPass));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ══════════════════════ /admin/students ════════════════════

    @Operation(summary = "List all students")
    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> listStudents() {
        var students = baseUserRepo.findAll().stream()
                .filter(u -> u.getStudent() != null)
                .map(this::toUserMap).toList();
        return ResponseEntity.ok(Map.of("students", students));
    }

    @Operation(summary = "Get student by user ID")
    @GetMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> getStudent(@PathVariable Long id) {
        return baseUserRepo.findById(id)
                .filter(u -> u.getStudent() != null)
                .map(u -> ResponseEntity.ok(Map.of("student", toUserMap(u))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update student status")
    @PatchMapping("/students/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStudentStatus(@PathVariable Long id,
                                                                   @RequestBody Map<String, String> body) {
        return baseUserRepo.findById(id)
                .filter(u -> u.getStudent() != null)
                .map(u -> {
                    u.setStatus(body.get("status"));
                    baseUserRepo.save(u);
                    auditLogService.log(actorName(), "changed_student_status",
                            "Student User ID: " + id + " → " + body.get("status"));
                    return ResponseEntity.ok(Map.of("student", toUserMap(u)));
                }).orElse(ResponseEntity.notFound().build());
    }

    // ══════════════════════ /admin/teachers ════════════════════

    @Operation(summary = "List all teachers")
    @GetMapping("/teachers")
    public ResponseEntity<Map<String, Object>> listTeachers() {
        var teachers = baseUserRepo.findAll().stream()
                .filter(u -> u.getTeacher() != null)
                .map(this::toUserMap).toList();
        return ResponseEntity.ok(Map.of("teachers", teachers));
    }

    @Operation(summary = "Get teacher by user ID")
    @GetMapping("/teachers/{id}")
    public ResponseEntity<Map<String, Object>> getTeacher(@PathVariable Long id) {
        return baseUserRepo.findById(id)
                .filter(u -> u.getTeacher() != null)
                .map(u -> ResponseEntity.ok(Map.of("teacher", toUserMap(u))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update teacher status")
    @PatchMapping("/teachers/{id}/status")
    public ResponseEntity<Map<String, Object>> updateTeacherStatus(@PathVariable Long id,
                                                                   @RequestBody Map<String, String> body) {
        return baseUserRepo.findById(id)
                .filter(u -> u.getTeacher() != null)
                .map(u -> {
                    u.setStatus(body.get("status"));
                    baseUserRepo.save(u);
                    auditLogService.log(actorName(), "changed_teacher_status",
                            "Teacher User ID: " + id + " → " + body.get("status"));
                    return ResponseEntity.ok(Map.of("teacher", toUserMap(u)));
                }).orElse(ResponseEntity.notFound().build());
    }
}
