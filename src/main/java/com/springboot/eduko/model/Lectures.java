package com.springboot.eduko.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private EduCourses eduCourses;
    @OneToMany(mappedBy = "lectures")
    private List<Assignments> assignments;
    @OneToMany(mappedBy = "lectures")
    private List<LectureAccess> lectureAccesses;
}
