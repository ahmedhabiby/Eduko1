package com.springboot.eduko.controller.vms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatableTeacher {
    private Long id ;
    private String email;
    private String teacherName;
}
