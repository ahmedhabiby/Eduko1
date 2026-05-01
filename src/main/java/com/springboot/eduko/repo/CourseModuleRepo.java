package com.springboot.eduko.repo;

import com.springboot.eduko.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseModuleRepo extends JpaRepository<CourseModule, Long> {

    List<CourseModule> findByCourseIdOrderByOrder(Long courseId);
}
