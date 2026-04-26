package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.AccessRequest;
import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;

public interface LectureAccessService {
    EnrollResponse giveAccess(AccessRequest request);

}
