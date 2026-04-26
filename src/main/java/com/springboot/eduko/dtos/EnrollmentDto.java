package com.springboot.eduko.dtos;

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
public class EnrollmentDto {
    private Student student;
    private EduCourses eduCourses;
}
