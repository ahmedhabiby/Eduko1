package com.springboot.eduko.controller.vms;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LectureRequest {
    @NotBlank(message = "lecture.link.notBlank")
    @URL(message = "lecture.link.invalid")
    private String lectureLink;

    @NotBlank(message = "lecture.title.notBlank")
    @Size(min = 3, max = 100, message = "lecture.title.size")
    @Column(unique = true)
    private String lectureTitle;

    @NotBlank(message = "course.title.notBlank")
    @Size(min = 3, max = 100, message = "course.title.size")
    private String courseTitle;
}
