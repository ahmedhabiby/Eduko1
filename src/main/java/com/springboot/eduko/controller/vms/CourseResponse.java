package com.springboot.eduko.controller.vms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.eduko.dtos.LectureDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {
    private String courseTitle;
    private String courseLink;
    @JsonManagedReference
    private List<LectureDto> lectures;
}
