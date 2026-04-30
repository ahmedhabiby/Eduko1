package com.springboot.eduko.controller.vms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestForStudent {

    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email must be valid like ahmed@gmail.com")
    private String email;

    @NotBlank(message = "Password must not be empty")
    private String password;

    // Optional fields — can be completed later via profile update
    private String parentName;
    private String parentNumber;
    private String studentNumber;
}
