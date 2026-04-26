package com.springboot.eduko.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.eduko.model.Enrollments;
import com.springboot.eduko.model.Lectures;
import com.springboot.eduko.model.Teacher;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDto {
    private Long id;
    private String courseTitle;
    private String courseLink;
    @JsonIgnore
    private List<Lectures> lectures;
    @JsonIgnore
    private List<Enrollments> enrollments;
    @JsonIgnore
    private Teacher teacher;
}
