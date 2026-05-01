package com.springboot.eduko.controller.admin;

import com.springboot.eduko.jwt.HandleToken;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.TokenBlackListRepo;
import com.springboot.eduko.service.AuditLogService;
import com.springboot.eduko.service.TokenBlackListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Admin — Auth", description = "Admin login & logout")
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final BaseUserRepo      baseUserRepo;
    private final PasswordEncoder   passwordEncoder;
    private final HandleToken       handleToken;
    private final TokenBlackListService tokenBlackListService;
    private final AuditLogService   auditLogService;

    @Autowired
    public AdminAuthController(BaseUserRepo baseUserRepo,
                               PasswordEncoder passwordEncoder,
                               HandleToken handleToken,
                               TokenBlackListService tokenBlackListService,
                               AuditLogService auditLogService) {
        this.baseUserRepo         = baseUserRepo;
        this.passwordEncoder      = passwordEncoder;
        this.handleToken          = handleToken;
        this.tokenBlackListService = tokenBlackListService;
        this.auditLogService      = auditLogService;
    }

    // ──────────────────────────────────────────────────────────────────
    @Operation(summary = "Admin Login",
               description = "Authenticate admin with email + password. Returns JWT token and admin profile.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {

        String email    = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "email and password are required"));
        }

        BaseUser user = baseUserRepo.findBaseUsersByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getRole()));
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied: admin role required"));
        }

        if ("banned".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Account is banned"));
        }

        user.setLastLoginAt(LocalDateTime.now().toString());
        baseUserRepo.save(user);

        com.springboot.eduko.dtos.BaseUserDto dto = new com.springboot.eduko.dtos.BaseUserDto();
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(r -> { var rd = new com.springboot.eduko.dtos.RoleDto(); rd.setRole(r.getRole()); return rd; })
                .toList());
        String token = handleToken.generateToken(dto);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id",    user.getId().toString());
        userMap.put("email", user.getEmail());
        userMap.put("name",  "Admin");
        userMap.put("role",  "admin");
        userMap.put("status", user.getStatus());

        auditLogService.log(email, "admin_login", "Admin logged in");

        return ResponseEntity.ok(Map.of("user", userMap, "token", token));
    }

    // ──────────────────────────────────────────────────────────────────
    @Operation(summary = "Admin Logout", description = "Invalidates the current admin JWT.")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenBlackListService.addToBlackList(authHeader.substring(7));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }
}
