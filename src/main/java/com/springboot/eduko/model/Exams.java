package com.springboot.eduko.model;

import jakarta.persistence.Entity;
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
public class Exams extends BaseEntity {
    private String examTitle;
    @OneToOne
    private Lectures lectures;
    @OneToMany(mappedBy = "exams")
    private List<ExamResult> examResultList;
}
