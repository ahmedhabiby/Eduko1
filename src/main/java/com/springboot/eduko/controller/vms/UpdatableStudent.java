package com.springboot.eduko.controller.vms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatableStudent {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String parentName;
    private String parentNumber;
    private String studentNumber;
}
