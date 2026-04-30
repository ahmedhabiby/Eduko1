package com.springboot.eduko.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents the middle tier in the Admin panel's
 * Course → Module → Lesson hierarchy.
 *
 * A module groups related lessons inside one course.
 * The Student platform currently accesses lectures directly;
 * this entity is used exclusively by the Admin API.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseModule extends BaseEntity {

    private String titleEn;   // English module title
    private String titleAr;   // Arabic module title
    private Integer order;    // display order within the course (1-based)

    @ManyToOne
    @JsonIgnore
    private EduCourses course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Lectures> lessons;
}
