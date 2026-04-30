package com.springboot.eduko.controller.vms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressResponse {
    private Long   courseId;
    private String courseTitle;
    private String thumbnailUrl;
    private int    progressPercent;   // 0-100
    private int    completedLessons;
    private int    totalLessons;
    private String lastAccessedAt;    // ISO-8601
}
