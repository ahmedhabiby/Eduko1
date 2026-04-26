package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.dtos.EnrollmentDto;

import java.util.List;

public interface EnrollmentService {
    EnrollResponse doEnrollments(EnrollRequest request);
    List<EnrollmentDto> getAllEnrollments();
}
