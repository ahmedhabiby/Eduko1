package com.springboot.eduko.controller.admin;

import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.PaymentProof;
import com.springboot.eduko.repo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Admin — Reports", description = "Dashboard statistics and analytics")
@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final BaseUserRepo     baseUserRepo;
    private final CourseRepo       courseRepo;
    private final EnrollmentRepo   enrollmentRepo;
    private final PaymentProofRepo paymentProofRepo;

    @Autowired
    public AdminReportController(BaseUserRepo baseUserRepo,
                                 CourseRepo courseRepo,
                                 EnrollmentRepo enrollmentRepo,
                                 PaymentProofRepo paymentProofRepo) {
        this.baseUserRepo     = baseUserRepo;
        this.courseRepo       = courseRepo;
        this.enrollmentRepo   = enrollmentRepo;
        this.paymentProofRepo = paymentProofRepo;
    }

    @Operation(summary = "Get dashboard statistics",
               description = "Returns totalStudents, totalTeachers, totalCourses, totalEnrollments, " +
                             "totalRevenue, completionRate, recentPayments, topCourses.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> reports() {

        var allUsers    = baseUserRepo.findAll();
        long totalStudents  = allUsers.stream().filter(u -> u.getStudent()  != null).count();
        long totalTeachers  = allUsers.stream().filter(u -> u.getTeacher()  != null).count();
        long totalCourses   = courseRepo.count();

        var allEnrollments  = enrollmentRepo.findAll();
        long totalEnrollments = allEnrollments.size();

        // Revenue: sum of approved payment proofs
        double totalRevenue = paymentProofRepo.findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> "approved".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                .sum();

        // Completion rate
        long completed = allEnrollments.stream()
                .filter(e -> "completed".equals(e.getStatus())).count();
        double completionRate = totalEnrollments > 0
                ? Math.round((completed * 100.0 / totalEnrollments) * 10.0) / 10.0
                : 0.0;

        // Recent 5 approved payments
        List<Map<String, Object>> recentPayments = paymentProofRepo
                .findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> "approved".equals(p.getStatus()))
                .limit(5)
                .map(p -> {
                    Map<String, Object> pm = new HashMap<>();
                    pm.put("id",          p.getId().toString());
                    pm.put("studentName", p.getStudentName());
                    pm.put("courseName",  p.getCourseName());
                    pm.put("amount",      p.getAmount());
                    pm.put("currency",    p.getCurrency());
                    pm.put("date",        p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
                    return pm;
                }).toList();

        // Top 5 courses by enrollment
        List<Map<String, Object>> topCourses = courseRepo.findAll().stream()
                .sorted(Comparator.comparingInt(
                        (EduCourses c) -> c.getEnrollments() != null ? c.getEnrollments().size() : 0)
                        .reversed())
                .limit(5)
                .map(c -> {
                    Map<String, Object> cm = new HashMap<>();
                    cm.put("id",           c.getId().toString());
                    cm.put("title",        c.getCourseTitle());
                    cm.put("enrollments",  c.getEnrollments() != null ? c.getEnrollments().size() : 0);
                    cm.put("rating",       c.getRating());
                    return cm;
                }).toList();

        Map<String, Object> report = new HashMap<>();
        report.put("totalStudents",   totalStudents);
        report.put("totalTeachers",   totalTeachers);
        report.put("totalCourses",    totalCourses);
        report.put("totalEnrollments", totalEnrollments);
        report.put("totalRevenue",    totalRevenue);
        report.put("completionRate",  completionRate);
        report.put("recentPayments",  recentPayments);
        report.put("topCourses",      topCourses);

        return ResponseEntity.ok(report);
    }
}
