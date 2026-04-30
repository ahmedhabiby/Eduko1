package com.springboot.eduko.model;

import jakarta.persistence.*;
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
public class EduCourses extends BaseEntity {

    private String courseTitle;
    private String courseLink;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String  thumbnailUrl;
    private Double  price;
    private String  currency;        // "EGP" | "USD"
    private String  level;           // "beginner" | "intermediate" | "advanced"
    private String  category;
    private String  instructorName;
    private Double  rating;
    private Integer enrolledCount;
    private String  status;          // "published" | "draft" | "archived"

    @OneToMany(mappedBy = "eduCourses")
    private List<Lectures> lectures;

    @OneToMany(mappedBy = "eduCourses")
    private List<Enrollments> enrollments;

    @ManyToOne
    private Teacher teacher;
}
