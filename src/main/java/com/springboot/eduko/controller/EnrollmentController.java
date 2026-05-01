package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.controller.vms.LessonProgressRequest;
import com.springboot.eduko.dtos.EnrollmentDto;
import com.springboot.eduko.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Enrollments", description = "Course enrollment management for the authenticated student")
@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // ══════════════════════ Enroll ══════════════════════

    @Operation(
            summary = "Enroll in a course",
            description = "Enrolls the authenticated student in a course. Body: { courseId: number }. " +
                          "Accessible via /doEnrollment (legacy) and /enrollments (canonical)."
    )
    @PostMapping({"/doEnrollment", "/enrollments"})
    public ResponseEntity<EnrollResponse> doEnrollments(@RequestBody EnrollRequest enrollmentRequest) {
        return ResponseEntity.ok(enrollmentService.doEnrollments(enrollmentRequest));
    }

    // ══════════════════════ List ══════════════════════

    @Operation(
            summary = "Get my enrollments",
            description = "Returns all course enrollments for the authenticated student. " +
                          "Accessible via /getAllEnrollmentsForAuthStudent (legacy), /enrollments/my (old alias), " +
                          "and /enrollments (canonical — used by frontend)."
    )
    @GetMapping({"/getAllEnrollmentsForAuthStudent", "/enrollments/my", "/enrollments"})
    public ResponseEntity<Map<String, Object>> getAllEnrollmentsForAuthStudent() {
        List<EnrollmentDto> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(Map.of("enrollments", enrollments));
    }

    // ══════════════════════ Progress ══════════════════════

    @Operation(
            summary = "Update lesson progress",
            description = "Marks a specific lesson as done or undone for an enrollment. " +
                          "Body: { lessonId: string, done: boolean }."
    )
    @PatchMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<EnrollResponse> updateLessonProgress(
            @PathVariable Long enrollmentId,
            @RequestBody LessonProgressRequest request) {
        return ResponseEntity.ok(enrollmentService.updateLessonProgress(enrollmentId, request));
    }
}
