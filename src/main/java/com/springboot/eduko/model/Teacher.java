package com.springboot.eduko.model;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Teacher extends BaseEntity {
    private String teacherName;
    @OneToOne(mappedBy = "teacher")
    private BaseUser baseUser;
    @OneToMany(mappedBy = "teacher")
    private List<EduCourses> eduCoursesList;
}
