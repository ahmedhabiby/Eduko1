package com.springboot.eduko.controller.vms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthTeacherResponse {
    private String email;
    private String teacherName;
}
