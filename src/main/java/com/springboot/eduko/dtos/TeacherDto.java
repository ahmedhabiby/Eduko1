package com.springboot.eduko.dtos;

import com.springboot.eduko.model.BaseUser;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDto {
    private Long id;
    private String teacherName;
    private BaseUser baseUser;
}
