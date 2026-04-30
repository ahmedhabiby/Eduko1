package com.springboot.eduko.repo;

import com.springboot.eduko.model.LectureAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureAccessRepo extends JpaRepository<LectureAccess, Long> {

    // Find a specific student's access record for a specific lecture
    Optional<LectureAccess> findByStudentIdAndLecturesId(Long studentId, Long lectureId);

    // All access records for a student in a specific course
    List<LectureAccess> findByStudentIdAndLecturesEduCoursesId(Long studentId, Long courseId);

    // Count completed lectures (status=2) for a student in a specific course
    long countByStudentIdAndLecturesEduCoursesIdAndStatus(Long studentId, Long courseId, int status);

    // All access records for a student (across all courses)
    List<LectureAccess> findByStudentId(Long studentId);
}
