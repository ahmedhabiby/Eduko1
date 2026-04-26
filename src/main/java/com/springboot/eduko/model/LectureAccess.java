package com.springboot.eduko.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LectureAccess extends BaseEntity {
    private int status;
    @ManyToOne
    private Student student;
    @ManyToOne
    private Lectures lectures;
}
