package com.springboot.eduko.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners( AuditingEntityListener.class)
@Setter
@Getter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @Column(insertable = false)
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(insertable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
