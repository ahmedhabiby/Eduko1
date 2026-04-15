package com.springboot.eduko.controller.vms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthStudentResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String parentName;
    private String parentNumber;
    private String studentNumber;
}
