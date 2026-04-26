package com.springboot.eduko.repo;

import com.springboot.eduko.model.Lectures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepo extends JpaRepository<Lectures,Long> {
    Lectures findByLectureTitle(String lectureTitle);
}
