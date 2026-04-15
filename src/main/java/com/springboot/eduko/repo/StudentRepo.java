package com.springboot.eduko.repo;

import com.springboot.eduko.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {
    @Query("SELECT s from Student  s where s.id=:id")
    Student getStudentById(@Param("id") Long id);
}
