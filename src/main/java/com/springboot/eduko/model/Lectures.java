package com.springboot.eduko.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lectures extends BaseEntity {

    // ── Legacy fields (student platform) ────────────────────────────────
    private String lectureLink;
    private String lectureTitle;         // English title (legacy)

    // ── Admin fields ─────────────────────────────────────────────────────
    private String  titleAr;             // Arabic title
    private Integer order;               // display order within module

    /**
     * Lesson type — matches Admin's Lesson.type.
     * Values: "video" | "pdf" | "quiz" | "assignment"
     */
    private String  type = "video";

    /** Google Drive file ID used by the admin video player. */
    private String  driveFileId;

    private String  startTimestamp;      // e.g. "00:00"
    private String  endTimestamp;        // e.g. "12:00"
    private Integer durationMin;         // duration in minutes
    private Boolean isFreePreview = false;

    // ── Relations ────────────────────────────────────────────────────────
    @ManyToOne
    @JsonBackReference
    private EduCourses eduCourses;

    /**
     * Module this lesson belongs to (Admin hierarchy).
     * Nullable for backward-compatible legacy lectures that aren't
     * assigned to a module yet.
     */
    @ManyToOne
    @JsonIgnore
    private CourseModule module;

    @JsonIgnore
    @OneToMany(mappedBy = "lectures")
    private List<Assignments> assignments;

    @OneToMany(mappedBy = "lectures")
    @JsonIgnore
    private List<LectureAccess> lectureAccesses;
}
