package com.springboot.eduko.controller.vms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestForTeachers {
    @NotBlank(message = "teacher.name.not.blank")
    @Size(max = 50, message = "teacher.name.size")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "teacher.name.invalid")
    private String teacherName;
    @NotBlank(message = "email.not.blank")
    @Email(message = "email.invalid")
    private String email;

    @NotBlank(message = "password.not.blank")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,20}$",
            message = "password.invalid"
    )
    private String password;

}
