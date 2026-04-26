package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.CourseRequest;
import com.springboot.eduko.controller.vms.CourseResponse;
import com.springboot.eduko.dtos.CourseDto;
import com.springboot.eduko.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class CourseController {
    private CourseService courseService;
    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }
    @PostMapping("/save/course")
    public ResponseEntity<CourseDto> saveCourse(@RequestBody @Valid CourseRequest courseRequest) throws URISyntaxException {
        return ResponseEntity.created(new URI("/save/course")).body(courseService.saveCourse(courseRequest));
    }
    @GetMapping("/get/Pages/Courses")
    public ResponseEntity<Page<CourseDto>> getCourses(@RequestParam int page,@RequestParam  int size) {
        return  ResponseEntity.ok(courseService.getCourses(page, size));
    }
    @GetMapping("/getCourseById")
    public ResponseEntity<CourseResponse> getCourseById(@RequestParam long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
}
