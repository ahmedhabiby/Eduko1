package com.springboot.eduko.repo;

import com.springboot.eduko.model.Lectures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepo extends JpaRepository<Lectures, Long> {

    Lectures findByLectureTitle(String lectureTitle);

    List<Lectures> findByModuleId(Long moduleId);

    List<Lectures> findByEduCoursesIdOrderByOrder(Long courseId);
}
