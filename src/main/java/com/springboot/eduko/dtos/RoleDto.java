package com.springboot.eduko.dtos;


import com.springboot.eduko.model.Student;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private Long id;
    private String role;
    private List<Student> students;

}
