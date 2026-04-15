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
public class Lectures extends BaseEntity{
    private String lectureLink;
    private String lectureTitle;
    @ManyToOne
    private EduCourses eduCourses;
    @OneToMany(mappedBy = "lectures")
    private List<Assignments> assignments;
}
