package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.AccessRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.dtos.LectureAccessDto;
import com.springboot.eduko.mapper.EnrollmentMapper;
import com.springboot.eduko.mapper.LectureAccessMapper;
import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.LectureAccess;
import com.springboot.eduko.model.Lectures;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.repo.*;
import com.springboot.eduko.service.LectureAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LectureAccessServiceImpl implements LectureAccessService {
    private LectureAccessRepo lectureAccessRepo;
    private final BaseUserRepo baseUserRepo;
    private final LectureRepo lectureRepo;
    private final StudentRepo studentRepo;
    private final LectureAccessMapper lectureAccessMapper;

    @Autowired
    public LectureAccessServiceImpl(BaseUserRepo baseUserRepo, LectureRepo lectureRepo, StudentRepo studentRepo,
                                    LectureAccessMapper lectureAccessMapper, LectureAccessRepo lectureAccessRepo) {
        this.baseUserRepo = baseUserRepo;
        this.lectureRepo = lectureRepo;
        this.studentRepo = studentRepo;
        this.lectureAccessMapper = lectureAccessMapper;
        this.lectureAccessRepo = lectureAccessRepo;
    }

    @Override
    public EnrollResponse giveAccess(AccessRequest request) {
        LectureAccessDto lectureAccessDto=new LectureAccessDto();
        Lectures lectures=lectureRepo.findByLectureTitle(request.getLectureTitle());
        if(Objects.isNull(lectures))
            throw new RuntimeException("lectures.not.found");
        BaseUser user = baseUserRepo.findBaseUsersByEmail(request.getStudentEmail());
        if (Objects.isNull(user))
            throw new RuntimeException("user.not.found");
        Student student=studentRepo.getStudentById(user.getStudent().getId());
        if (Objects.isNull(student))
            throw new RuntimeException("student.not.found");
        lectureAccessDto.setStatus(1);
        lectureAccessDto.setStudent(student);
        lectureAccessDto.setLectures(lectures);
        lectureAccessRepo.save(lectureAccessMapper.toEntity(lectureAccessDto));
        return new EnrollResponse("Access granted to student");
    }
}
