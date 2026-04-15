package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Assignments extends BaseEntity {
    private String assignmentTitle;
    @OneToMany(mappedBy = "assignments")
    private List<Submissions> submissionsList;
    @ManyToOne
    private Lectures lectures;
}
