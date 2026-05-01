package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.AuthStudentResponse;
import com.springboot.eduko.controller.vms.UpdatableStudent;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.StudentRepo;
import com.springboot.eduko.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Student", description = "Student profile and account management")
@RestController
public class StudentController {

    private final StudentService  studentService;
    private final BaseUserRepo    baseUserRepo;
    private final StudentRepo     studentRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentController(StudentService studentService,
                             BaseUserRepo baseUserRepo,
                             StudentRepo studentRepo,
                             PasswordEncoder passwordEncoder) {
        this.studentService  = studentService;
        this.baseUserRepo    = baseUserRepo;
        this.studentRepo     = studentRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private String actorEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ══════════════════════ Profile ══════════════════════

    @Operation(summary = "Get current student profile",
               description = "Returns profile of the authenticated student. Accessible via /auth/user (legacy) and /users/me (new).")
    @GetMapping({"/auth/user", "/users/me"})
    public ResponseEntity<AuthStudentResponse> authUser() {
        return ResponseEntity.ok(studentService.getAuthStudent());
    }

    @Operation(summary = "Update student profile",
               description = "Updates name, phone, parentName, etc. Accessible via /update/student (legacy) and /users/me (new).")
    @PatchMapping({"/users/me", "/update/student"})
    public ResponseEntity<UpdatableStudent> updateStudent(
            @RequestBody AuthStudentResponse authStudentResponse) {
        return ResponseEntity.ok(studentService.updateStudent(authStudentResponse));
    }

    // ══════════════════════ Password ══════════════════════

    @Operation(summary = "Change password (authenticated)",
               description = "Requires oldPassword + newPassword. Both must be non-blank. JWT must be valid.")
    @PatchMapping("/users/me/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody Map<String, String> body) {

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || oldPassword.isBlank() ||
            newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "oldPassword and newPassword are required"));
        }

        BaseUser user = baseUserRepo.findBaseUsersByEmail(actorEmail());
        if (user == null) return ResponseEntity.notFound().build();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        baseUserRepo.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }
}
