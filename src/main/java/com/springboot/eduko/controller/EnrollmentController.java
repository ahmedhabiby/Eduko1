package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.dtos.EnrollmentDto;
import com.springboot.eduko.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
    @PostMapping("/doEnrollment")
    public ResponseEntity<EnrollResponse> doEnrollments(@RequestBody EnrollRequest enrollmentRequest) {
        return ResponseEntity.ok(enrollmentService.doEnrollments(enrollmentRequest));
    }
    @GetMapping("/getAllEnrollmentsForAuthStudent")
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollmentsForAuthStudent() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

}
