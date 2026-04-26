package com.springboot.eduko.repo;

import com.springboot.eduko.model.Enrollments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollments, Long> {
}
