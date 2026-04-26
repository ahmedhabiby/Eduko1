package com.springboot.eduko.repo;

import com.springboot.eduko.model.LectureAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureAccessRepo extends JpaRepository<LectureAccess,Long> {
}
