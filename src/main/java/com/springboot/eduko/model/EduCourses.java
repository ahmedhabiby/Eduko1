package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EduCourses extends BaseEntity{
    private String courseTitle;
    private String courseLink;
    @OneToMany(mappedBy = "eduCourses")
    private List<Lectures> lectures;
    @OneToMany(mappedBy = "eduCourses")
    private List<Enrollments> enrollments;
    @ManyToOne
    private Teacher teacher;

}
