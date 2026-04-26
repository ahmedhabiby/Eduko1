package com.springboot.eduko.repo;

import com.springboot.eduko.model.EduCourses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<EduCourses, Long> {
    EduCourses findEduCoursesByCourseTitle(String courseTitle);
    Page<EduCourses> findAll(Pageable pageable);
    EduCourses findEduCoursesById(long id);
}
