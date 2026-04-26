package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.EnrollRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.dtos.EnrollmentDto;
import com.springboot.eduko.mapper.EnrollmentMapper;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Enrollments;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.EnrollmentRepo;
import com.springboot.eduko.repo.StudentRepo;
import com.springboot.eduko.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepo enrollmentRepo;
    private final BaseUserRepo baseUserRepo;
    private final CourseRepo courseRepo;
    private final StudentRepo studentRepo;
    private final EnrollmentMapper enrollmentMapper;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepo enrollmentRepo, BaseUserRepo baseUserRepo, CourseRepo courseRepo, StudentRepo studentRepo, EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepo = enrollmentRepo;
        this.baseUserRepo = baseUserRepo;
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Override
    public EnrollResponse doEnrollments(EnrollRequest request) {
        EnrollmentDto enrollmentDto = new EnrollmentDto();
        EduCourses courses=courseRepo.findEduCoursesByCourseTitle(request.getCourseTitle());
        if (Objects.isNull(courses))
            throw new RuntimeException("course.not.found");

        BaseUser user = baseUserRepo.findBaseUsersByEmail(request.getStudentEmail());
        if (Objects.isNull(user))
            throw new RuntimeException("user.not.found");
        Student student=studentRepo.getStudentById(user.getStudent().getId());
        if (Objects.isNull(student))
            throw new RuntimeException("student.not.found");
        enrollmentDto.setStudent(student);
        enrollmentDto.setEduCourses(courses);
        enrollmentRepo.save(enrollmentMapper.toEntity(enrollmentDto));
        return new EnrollResponse("Enrollments saved successfully");

    }
}
