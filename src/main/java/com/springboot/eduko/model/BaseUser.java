package com.springboot.eduko.model;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseUser extends BaseEntity{
    private String email;
    private String password;
    @ManyToMany
    private List<EduRoles> roles;
    @OneToOne
    private Student student;
    @OneToOne
    private Teacher teacher;
}
