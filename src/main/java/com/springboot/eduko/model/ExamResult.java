package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamResult extends BaseEntity {
    private double grade;
    @ManyToOne
    private Exams exams;
    @ManyToOne
    private Student student;
}
