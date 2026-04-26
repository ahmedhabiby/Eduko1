package com.springboot.eduko.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentDto {
    private StudentDto student;
    private CourseDto eduCourses;
}
