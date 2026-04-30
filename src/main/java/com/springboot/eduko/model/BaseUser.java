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
public class BaseUser extends BaseEntity {
    private String email;
    private String password;

    /**
     * Account status used by the Admin panel.
     * Values: "active" | "inactive" | "banned"
     * Default is set by AdminUserController on creation.
     */
    private String status = "active";

    /** ISO-8601 timestamp of the last successful login. */
    private String lastLoginAt;

    @ManyToMany
    private List<EduRoles> roles;

    @OneToOne
    private Student student;

    @OneToOne
    private Teacher teacher;
}
