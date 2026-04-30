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

@Tag(name = "Enrollments", description = "Course enrollment management")
@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(
            summary = "Enroll in a course",
            description = "Enrolls the authenticated student in a course by courseId. Student is resolved from JWT token."
    )
    @PostMapping({"/doEnrollment", "/enrollments"})
    public ResponseEntity<EnrollResponse> doEnrollments(@RequestBody EnrollRequest enrollmentRequest) {
        return ResponseEntity.ok(enrollmentService.doEnrollments(enrollmentRequest));
    }

    @Operation(
            summary = "Get my enrollments",
            description = "Returns all course enrollments for the authenticated student."
    )
    @GetMapping({"/getAllEnrollmentsForAuthStudent", "/enrollments/my"})
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollmentsForAuthStudent() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @Operation(
            summary = "Update lesson progress",
            description = "Marks a lesson as completed or not. TODO: requires LessonProgress tracking table."
    )
    @PatchMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<EnrollResponse> updateLessonProgress(
            @PathVariable Long enrollmentId,
            @RequestBody LessonProgressRequest request) {
        return ResponseEntity.ok(enrollmentService.updateLessonProgress(enrollmentId, request));
    }
}
