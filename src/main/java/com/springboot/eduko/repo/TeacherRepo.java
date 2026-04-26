package com.springboot.eduko.repo;

import com.springboot.eduko.model.Student;
import com.springboot.eduko.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepo extends JpaRepository<Teacher,Long> {
    @Query("SELECT t from Teacher  t where t.id=:id")
    Teacher getTeacherById(@Param("id") Long id);

    Teacher findTeacherByTeacherName(String teacherName);
}
