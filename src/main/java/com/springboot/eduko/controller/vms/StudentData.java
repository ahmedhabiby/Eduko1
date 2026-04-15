package com.springboot.eduko.controller.vms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class StudentData {
    private String firstName;
    private String lastName;
    private String parentName;
    private String parentNumber;
    private String studentNumber;
}
