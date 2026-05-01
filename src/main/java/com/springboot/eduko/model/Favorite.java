package com.springboot.eduko.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"})
)
@Getter
@Setter
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BaseUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private EduCourses course;

    /**
     * ISO-8601 timestamp string, e.g. "2026-05-01T09:00:00"
     * Stored as plain String so the controller can return it directly
     * without extra serialization.
     */
    @Column(name = "saved_at")
    private String savedAt;
}
