package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class EduRoles extends BaseEntity {
    private String role;
    @ManyToMany(mappedBy = "roles")
    private List<BaseUser> users;
}
