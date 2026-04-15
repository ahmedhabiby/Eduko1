package com.springboot.eduko.controller.vms;
import jakarta.validation.constraints.Email;
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
public class SignupRequestForStudent {


    @NotBlank(message = "email.not.blank")
    @Email(message = "email.invalid")
    private String email;

    @NotBlank(message = "password.not.blank")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,20}$",
            message = "password.invalid"
    )
    private String password;
    @NotBlank(message = "First name must not be empty")
    @Size(max = 30, message = "First name must not exceed 30 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    @Size(max = 30, message = "Last name must not exceed 30 characters")
    private String lastName;


    @NotBlank(message = "Parent name is required")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Parent name must contain only letters and spaces")
    private String parentName;

    @NotBlank(message = "Parent phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Invalid phone number format"
    )
    private String parentNumber;
    @NotBlank(message = "StudentData number is required")
    @Pattern(regexp = "\\d+", message = "StudentData number must contain only digits")
    @Size(min = 6, max = 10, message = "StudentData number must be 6 to 10 digits")
    private String studentNumber;
}
