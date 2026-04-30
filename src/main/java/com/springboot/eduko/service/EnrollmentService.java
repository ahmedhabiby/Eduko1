package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.controller.vms.LessonProgressRequest;
import com.springboot.eduko.controller.vms.ProgressResponse;
import com.springboot.eduko.dtos.EnrollmentDto;

import java.util.List;

public interface EnrollmentService {
    EnrollResponse doEnrollments(EnrollRequest enrollmentRequest);
    List<EnrollmentDto> getAllEnrollments();
    List<ProgressResponse> getProgressForAuthStudent();
    ProgressResponse getCourseProgress(Long courseId);
    EnrollResponse updateLessonProgress(Long enrollmentId, LessonProgressRequest request);
}
