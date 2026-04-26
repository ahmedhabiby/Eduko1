package com.springboot.eduko.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.eduko.model.Assignments;
import com.springboot.eduko.model.EduCourses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureDto {
    private Long id;
    private String lectureLink;
    private String lectureTitle;
    @JsonIgnore
    private List<Assignments> assignments;
    @JsonIgnore
    private EduCourses eduCourses;

}
