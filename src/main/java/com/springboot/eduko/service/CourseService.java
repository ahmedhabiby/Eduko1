package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.CourseRequest;
import com.springboot.eduko.controller.vms.CourseResponse;
import com.springboot.eduko.dtos.CourseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {
    CourseDto saveCourse(CourseRequest request);
    Page<CourseDto> getCourses(int  page, int size);
    CourseResponse getCourseById(long id);

}
