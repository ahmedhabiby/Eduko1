package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.LectureRequest;
import com.springboot.eduko.dtos.LectureDto;
import com.springboot.eduko.mapper.LectureMapper;
import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Lectures;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.LectureRepo;
import com.springboot.eduko.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LectureServiceImpl implements LectureService {
    private final LectureMapper lectureMapper;
    private final LectureRepo lectureRepo;
    private final CourseRepo courseRepo;

    @Autowired
    public LectureServiceImpl(LectureMapper lectureMapper, LectureRepo lectureRepo, CourseRepo courseRepo) {
        this.lectureMapper = lectureMapper;
        this.lectureRepo = lectureRepo;
        this.courseRepo = courseRepo;
    }

    @Override
    public LectureDto saveLecture(LectureRequest lectureRequest) {
        Lectures lectures1 = lectureRepo.findByLectureTitle(lectureRequest.getLectureTitle());
        if (Objects.nonNull(lectures1))
            throw new RuntimeException("lecture.exists");
        EduCourses eduCourses = courseRepo.findEduCoursesByCourseTitle(lectureRequest.getCourseTitle());
        if(Objects.isNull(eduCourses))
            throw new RuntimeException("course.not.found");
        LectureDto lectureDto = new LectureDto();
        lectureDto.setLectureTitle(lectureRequest.getLectureTitle());
        lectureDto.setLectureLink(lectureRequest.getLectureLink());
        lectureDto.setEduCourses(eduCourses);
        Lectures lectures=lectureRepo.save(lectureMapper.toEntity(lectureDto));
        return lectureMapper.toDto(lectures);
    }
}
