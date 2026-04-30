package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.*;
import com.springboot.eduko.dtos.EnrollmentDto;
import com.springboot.eduko.mapper.EnrollmentMapper;
import com.springboot.eduko.mapper.StudentMapper;
import com.springboot.eduko.model.*;
import com.springboot.eduko.repo.*;
import com.springboot.eduko.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    // status constants for LectureAccess
    private static final int STATUS_NOT_STARTED = 0;
    private static final int STATUS_IN_PROGRESS  = 1;
    private static final int STATUS_COMPLETED    = 2;

    private final EnrollmentRepo    enrollmentRepo;
    private final BaseUserRepo      baseUserRepo;
    private final CourseRepo        courseRepo;
    private final StudentRepo       studentRepo;
    private final LectureRepo       lectureRepo;
    private final LectureAccessRepo lectureAccessRepo;
    private final EnrollmentMapper  enrollmentMapper;
    private final StudentMapper     studentMapper;

    @Autowired
    public EnrollmentServiceImpl(
            EnrollmentRepo enrollmentRepo,
            BaseUserRepo baseUserRepo,
            CourseRepo courseRepo,
            StudentRepo studentRepo,
            LectureRepo lectureRepo,
            LectureAccessRepo lectureAccessRepo,
            EnrollmentMapper enrollmentMapper,
            StudentMapper studentMapper) {
        this.enrollmentRepo    = enrollmentRepo;
        this.baseUserRepo      = baseUserRepo;
        this.courseRepo        = courseRepo;
        this.studentRepo       = studentRepo;
        this.lectureRepo       = lectureRepo;
        this.lectureAccessRepo = lectureAccessRepo;
        this.enrollmentMapper  = enrollmentMapper;
        this.studentMapper     = studentMapper;
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private BaseUser getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BaseUser user = baseUserRepo.findBaseUsersByEmail(auth.getName());
        if (Objects.isNull(user))
            throw new RuntimeException("user.not.found");
        return user;
    }

    private Student getAuthenticatedStudent() {
        BaseUser user = getAuthenticatedUser();
        if (Objects.isNull(user.getStudent()))
            throw new RuntimeException("student.not.found");
        return studentRepo.getStudentById(user.getStudent().getId());
    }

    private ProgressResponse buildProgressResponse(Enrollments enrollment, Long studentId) {
        EduCourses course     = enrollment.getEduCourses();
        int totalLessons      = (course.getLectures() != null) ? course.getLectures().size() : 0;
        int completedLessons  = (int) lectureAccessRepo
                .countByStudentIdAndLecturesEduCoursesIdAndStatus(
                        studentId, course.getId(), STATUS_COMPLETED);
        int progressPercent   = (totalLessons > 0)
                ? (completedLessons * 100 / totalLessons)
                : 0;
        String lastAccessed   = enrollment.getUpdatedAt() != null
                ? enrollment.getUpdatedAt().toString()
                : (enrollment.getCreatedAt() != null
                        ? enrollment.getCreatedAt().toString() : null);
        return new ProgressResponse(
                course.getId(),
                course.getCourseTitle(),
                course.getThumbnailUrl(),
                progressPercent,
                completedLessons,
                totalLessons,
                lastAccessed
        );
    }

    // ─── Enrollment ─────────────────────────────────────────────────────

    @Override
    public EnrollResponse doEnrollments(EnrollRequest request) {
        Student student = getAuthenticatedStudent();

        EduCourses course = courseRepo.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("course.not.found"));

        Enrollments enrollment = new Enrollments();
        enrollment.setStudent(student);
        enrollment.setEduCourses(course);
        enrollment.setStatus("active");
        Enrollments saved = enrollmentRepo.save(enrollment);

        return new EnrollResponse(
                saved.getId(),
                course.getId(),
                course.getCourseTitle(),
                saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : null,
                saved.getStatus()
        );
    }

    @Override
    public List<EnrollmentDto> getAllEnrollments() {
        Student student = getAuthenticatedStudent();
        List<Enrollments> enrollments = enrollmentRepo.findByStudentId(student.getId());
        return enrollmentMapper.toDto(enrollments);
    }

    // ─── Progress ─────────────────────────────────────────────────────

    @Override
    public List<ProgressResponse> getProgressForAuthStudent() {
        Student student = getAuthenticatedStudent();
        List<Enrollments> enrollments = enrollmentRepo.findByStudentId(student.getId());
        return enrollments.stream()
                .map(e -> buildProgressResponse(e, student.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public ProgressResponse getCourseProgress(Long courseId) {
        Student student = getAuthenticatedStudent();
        List<Enrollments> enrollments = enrollmentRepo.findByStudentId(student.getId());
        Enrollments enrollment = enrollments.stream()
                .filter(e -> e.getEduCourses() != null
                        && e.getEduCourses().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("enrollment.not.found"));
        return buildProgressResponse(enrollment, student.getId());
    }

    @Override
    public EnrollResponse updateLessonProgress(Long enrollmentId, LessonProgressRequest request) {
        Student student = getAuthenticatedStudent();

        Enrollments enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("enrollment.not.found"));

        // Verify this enrollment belongs to the authenticated student
        if (!enrollment.getStudent().getId().equals(student.getId()))
            throw new RuntimeException("access.denied");

        Lectures lecture = lectureRepo.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("lecture.not.found"));

        // Upsert: find existing LectureAccess or create new
        Optional<LectureAccess> existingAccess =
                lectureAccessRepo.findByStudentIdAndLecturesId(student.getId(), lecture.getId());

        LectureAccess access = existingAccess.orElseGet(() -> {
            LectureAccess newAccess = new LectureAccess();
            newAccess.setStudent(student);
            newAccess.setLectures(lecture);
            return newAccess;
        });

        access.setStatus(request.isCompleted() ? STATUS_COMPLETED : STATUS_IN_PROGRESS);
        lectureAccessRepo.save(access);

        EduCourses course = enrollment.getEduCourses();
        return new EnrollResponse(
                enrollment.getId(),
                course.getId(),
                course.getCourseTitle(),
                enrollment.getCreatedAt() != null ? enrollment.getCreatedAt().toString() : null,
                enrollment.getStatus()
        );
    }
}
