package com.springboot.eduko.controller.vms;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
    @NotBlank(message = "course.title.notBlank")
    @Size(min = 3, max = 100, message = "course.title.size")
    @Column(unique = true)
    private String courseTitle;

    @NotBlank(message = "course.link.notBlank")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "course.link.invalid"
    )
    private String courseLink;

    @NotBlank(message = "teacher.name.notBlank")
    @Size(min = 3, max = 50, message = "teacher.name.size")
    private String teacherName;
}
