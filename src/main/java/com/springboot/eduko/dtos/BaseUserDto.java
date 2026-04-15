package com.springboot.eduko.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.eduko.model.EduRoles;
import com.springboot.eduko.model.Student;
import com.springboot.eduko.model.Teacher;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUserDto {
    private Long id;
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email must be valid like ahmed@gmail.com")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,20}$",
            message = "Password must be 6-20 characters, include at least one letter and one number"
    )
    private String password;
    @JsonIgnore
    private List<EduRoles> roles;
    @JsonIgnore
    private Student student;
    @JsonIgnore
    private Teacher teacher;

}
