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

    // ── Bilingual title & description ────────────────────────────────────
    /** English title — also used as the legacy courseTitle field. */
    private String courseTitle;
    /** Arabic title (required by Admin panel). */
    private String titleAr;

    @Column(columnDefinition = "TEXT")
    private String description;          // English description

    @Column(columnDefinition = "TEXT")
    private String descriptionAr;        // Arabic description

    // ── Media & pricing ──────────────────────────────────────────────────
    private String  courseLink;
    private String  thumbnailUrl;
    private Double  price;
    private String  currency;            // "EGP" | "USD"

    // ── Classification ───────────────────────────────────────────────────
    private String  level;               // "beginner" | "intermediate" | "advanced"
    private String  category;            // "Tech" | "Design" | "Business" ...

    // ── Instructor / Teacher ─────────────────────────────────────────────
    /** Denormalised teacher name for quick display (kept in sync by service). */
    private String  instructorName;

    // ── Stats ────────────────────────────────────────────────────────────
    private Double  rating;
    private Integer enrolledCount;

    // ── Lifecycle ────────────────────────────────────────────────────────
    /** "published" | "draft" | "archived" */
    private String  status;

    /**
     * When true, admin must manually approve enrollment (paid courses).
     * Free courses have requiresApproval = false.
     */
    private Boolean requiresApproval = false;

    // ── Relations ────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "eduCourses", cascade = CascadeType.ALL)
    private List<Lectures> lectures;

    /** Admin-facing Course → Module → Lesson hierarchy. */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseModule> modules;

    @OneToMany(mappedBy = "eduCourses")
    private List<Enrollments> enrollments;

    @ManyToOne
    private Teacher teacher;
}
