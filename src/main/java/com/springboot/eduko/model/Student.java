package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
public class Student extends BaseEntity{
    private String firstName;
    private String lastName;
    private String studentNumber;
    private String parentName;
    private String parentNumber;
    @OneToOne(mappedBy = "student")
    private BaseUser baseUser;
    @OneToMany(mappedBy = "student")
    private List<Enrollments>enrollments;
    @OneToMany(mappedBy = "student")
    private List<ExamResult> examResultList;
    @OneToMany(mappedBy = "student")
    private List<Submissions> submissionsList;
}
