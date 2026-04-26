package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.CourseRequest;
import com.springboot.eduko.controller.vms.CourseResponse;
import com.springboot.eduko.dtos.CourseDto;
import com.springboot.eduko.mapper.CourseMapper;
import com.springboot.eduko.mapper.LectureMapper;
import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Teacher;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.TeacherRepo;
import com.springboot.eduko.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepo courseRepo;
    private final CourseMapper courseMapper;
    private  final TeacherRepo teacherRepo;
    private final LectureMapper lectureMapper;

    @Autowired
    public CourseServiceImpl(CourseRepo courseRepo, CourseMapper courseMapper, TeacherRepo teacherRepo, LectureMapper lectureMapper) {
        this.courseRepo = courseRepo;
        this.courseMapper = courseMapper;
        this.teacherRepo = teacherRepo;
        this.lectureMapper = lectureMapper;
    }

    @Override
    public CourseDto saveCourse(CourseRequest request) {
        EduCourses courses = courseRepo.findEduCoursesByCourseTitle(request.getCourseTitle());
        if(Objects.nonNull(courses)){
            throw new RuntimeException("courses.already.exist");
        }
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseLink(request.getCourseLink());
        courseDto.setCourseTitle(request.getCourseTitle());
        Teacher teacher = teacherRepo.findTeacherByTeacherName(request.getTeacherName());
        if(Objects.isNull(teacher)){
            throw new RuntimeException("teacher.not.found");
        }
        courseDto.setTeacher(teacher);
        EduCourses  eduCourses = courseRepo.save(courseMapper.toEntity(courseDto));
        return  courseMapper.toDto(eduCourses);
    }

    @Override
    public Page<CourseDto> getCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<EduCourses>  courses = courseRepo.findAll(pageable);
        return courses.map(courseMapper::toDto);
    }

    @Override
    public CourseResponse getCourseById(long id) {
        EduCourses courses = courseRepo.findEduCoursesById(id);
        if(Objects.isNull(courses))
            throw new RuntimeException("courses.not.found");
        return new CourseResponse(courses.getCourseTitle(), courses.getCourseLink(), lectureMapper.toDto(courses.getLectures()));
    }
}
