package com.springboot.eduko.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lectures extends BaseEntity{
    private String lectureLink;
    private String lectureTitle;
    @ManyToOne
    @JsonBackReference
    private EduCourses eduCourses;
    @JsonIgnore
    @OneToMany(mappedBy = "lectures")
    private List<Assignments> assignments;
    @OneToMany(mappedBy = "lectures")
    @JsonIgnore
    private List<LectureAccess> lectureAccesses;
}
