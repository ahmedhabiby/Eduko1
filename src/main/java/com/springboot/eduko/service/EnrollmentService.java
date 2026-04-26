package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;

public interface EnrollmentService {
    EnrollResponse doEnrollments(EnrollRequest request);
}
