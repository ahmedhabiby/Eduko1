package com.springboot.eduko.controller.vms;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private Long    id;
    private String  courseTitle;
    private String  courseLink;
    private String  description;
    private String  thumbnailUrl;
    private Double  price;
    private String  currency;
    private String  level;
    private String  category;
    private String  instructorName;
    private Double  rating;
    private Integer enrolledCount;
    private String  status;
    private List<LectureDto> lectures;
}
