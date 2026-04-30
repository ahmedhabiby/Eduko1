package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.ProgressResponse;
import com.springboot.eduko.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Progress", description = "Student course progress tracking")
@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public ProgressController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(
            summary = "Get all courses progress",
            description = "Returns progress data for all enrolled courses of the authenticated student."
    )
    @GetMapping
    public ResponseEntity<List<ProgressResponse>> getAllProgress() {
        return ResponseEntity.ok(enrollmentService.getProgressForAuthStudent());
    }

    @Operation(
            summary = "Get progress for a specific course",
            description = "Returns progress data for a specific enrolled course."
    )
    @GetMapping("/{courseId}")
    public ResponseEntity<ProgressResponse> getCourseProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseProgress(courseId));
    }
}
