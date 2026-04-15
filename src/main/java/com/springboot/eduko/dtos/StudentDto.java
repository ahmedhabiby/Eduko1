package com.springboot.eduko.dtos;

import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.EduRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StudentDto {
    private Long id;
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
    private BaseUser baseUser;

}
