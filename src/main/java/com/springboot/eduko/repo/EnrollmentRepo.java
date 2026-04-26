package com.springboot.eduko.repo;

import com.springboot.eduko.model.Enrollments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollments, Long> {
    List<Enrollments> findByStudentId(Long id);
}
