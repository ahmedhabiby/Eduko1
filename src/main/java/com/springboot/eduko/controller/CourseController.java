package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.CourseRequest;
import com.springboot.eduko.controller.vms.CourseResponse;
import com.springboot.eduko.dtos.CourseDto;
import com.springboot.eduko.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Tag(name = "Courses", description = "Browse and manage courses")
@RestController
public class CourseController {

    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(
            summary = "Create a new course",
            description = "Creates a course. Requires teacher authentication."
    )
    @PostMapping({"/save/course", "/courses"})
    public ResponseEntity<CourseDto> saveCourse(
            @RequestBody @Valid CourseRequest courseRequest) throws URISyntaxException {
        return ResponseEntity.created(new URI("/courses")).body(courseService.saveCourse(courseRequest));
    }

    @Operation(
            summary = "Get all courses with pagination",
            description = "Returns a paginated list of all published courses."
    )
    @GetMapping({"/get/Pages/Courses", "/courses"})
    public ResponseEntity<Page<CourseDto>> getCourses(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(courseService.getCourses(page, size));
    }

    @Operation(
            summary = "Get course by ID (query param)",
            description = "Returns course details with lectures. Use /courses/{id} for RESTful access."
    )
    @GetMapping("/getCourseById")
    public ResponseEntity<CourseResponse> getCourseById(@RequestParam long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(
            summary = "Get course by ID (RESTful path)",
            description = "Returns full course details including lectures."
    )
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> getCourseByPathId(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
}
