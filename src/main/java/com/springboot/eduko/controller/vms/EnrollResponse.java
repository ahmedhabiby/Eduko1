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
public class EnrollResponse {
    private Long   id;
    private Long   courseId;
    private String courseTitle;
    private String enrolledAt;   // ISO-8601 timestamp
    private String status;       // "active" | "completed" | "expired"
}
