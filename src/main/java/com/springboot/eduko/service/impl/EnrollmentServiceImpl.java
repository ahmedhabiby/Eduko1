package com.springboot.eduko.service.impl;

import com.springboot.eduko.controller.vms.*;
import com.springboot.eduko.dtos.EnrollmentDto;
import com.springboot.eduko.mapper.EnrollmentMapper;
import com.springboot.eduko.mapper.StudentMapper;
import com.springboot.eduko.model.*;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.EnrollmentRepo;
import com.springboot.eduko.repo.StudentRepo;
import com.springboot.eduko.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepo enrollmentRepo;
    private final BaseUserRepo   baseUserRepo;
    private final CourseRepo     courseRepo;
    private final StudentRepo    studentRepo;
    private final EnrollmentMapper enrollmentMapper;
    private final StudentMapper    studentMapper;

    @Autowired
    public EnrollmentServiceImpl(
            EnrollmentRepo enrollmentRepo,
            BaseUserRepo baseUserRepo,
            CourseRepo courseRepo,
            StudentRepo studentRepo,
            EnrollmentMapper enrollmentMapper,
            StudentMapper studentMapper) {
        this.enrollmentRepo  = enrollmentRepo;
        this.baseUserRepo    = baseUserRepo;
        this.courseRepo      = courseRepo;
        this.studentRepo     = studentRepo;
        this.enrollmentMapper = enrollmentMapper;
        this.studentMapper   = studentMapper;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private BaseUser getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        BaseUser user = baseUserRepo.findBaseUsersByEmail(email);
        if (Objects.isNull(user))
            throw new RuntimeException("user.not.found");
        return user;
    }

    // ─── Enrollment ───────────────────────────────────────────────────────────

    @Override
    public EnrollResponse doEnrollments(EnrollRequest request) {
        BaseUser user = getAuthenticatedUser();
        if (Objects.isNull(user.getStudent()))
            throw new RuntimeException("student.not.found");

        // Find course by ID (not courseTitle)
        EduCourses course = courseRepo.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("course.not.found"));

        Student student = studentRepo.getStudentById(user.getStudent().getId());
        if (Objects.isNull(student))
            throw new RuntimeException("student.not.found");

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
        BaseUser user = getAuthenticatedUser();
        if (Objects.isNull(user.getStudent()))
            throw new RuntimeException("student.not.found");
        List<Enrollments> enrollments = enrollmentRepo.findByStudentId(user.getStudent().getId());
        return enrollmentMapper.toDto(enrollments);
    }

    // ─── Progress ─────────────────────────────────────────────────────────────

    @Override
    public List<ProgressResponse> getProgressForAuthStudent() {
        BaseUser user = getAuthenticatedUser();
        if (Objects.isNull(user.getStudent()))
            throw new RuntimeException("student.not.found");

        List<Enrollments> enrollments = enrollmentRepo.findByStudentId(user.getStudent().getId());

        return enrollments.stream().map(enrollment -> {
            EduCourses course = enrollment.getEduCourses();
            int totalLessons = (course.getLectures() != null) ? course.getLectures().size() : 0;
            // TODO: Track completed lectures per student in a separate table
            int completedLessons = 0;
            int progressPercent  = totalLessons > 0
                    ? (completedLessons * 100 / totalLessons)
                    : 0;
            String lastAccessed = enrollment.getUpdatedAt() != null
                    ? enrollment.getUpdatedAt().toString()
                    : (enrollment.getCreatedAt() != null ? enrollment.getCreatedAt().toString() : null);
            return new ProgressResponse(
                    course.getId(),
                    course.getCourseTitle(),
                    course.getThumbnailUrl(),
                    progressPercent,
                    completedLessons,
                    totalLessons,
                    lastAccessed
            );
        }).collect(Collectors.toList());
    }

    @Override
    public ProgressResponse getCourseProgress(Long courseId) {
        BaseUser user = getAuthenticatedUser();
        if (Objects.isNull(user.getStudent()))
            throw new RuntimeException("student.not.found");

        List<Enrollments> enrollments = enrollmentRepo.findByStudentId(user.getStudent().getId());
        Enrollments enrollment = enrollments.stream()
                .filter(e -> e.getEduCourses() != null
                        && e.getEduCourses().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("enrollment.not.found"));

        EduCourses course = enrollment.getEduCourses();
        int totalLessons = (course.getLectures() != null) ? course.getLectures().size() : 0;
        // TODO: Track completed lectures per student in a separate table
        int completedLessons = 0;
        int progressPercent  = totalLessons > 0 ? (completedLessons * 100 / totalLessons) : 0;
        String lastAccessed = enrollment.getUpdatedAt() != null
                ? enrollment.getUpdatedAt().toString()
                : (enrollment.getCreatedAt() != null ? enrollment.getCreatedAt().toString() : null);

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

    @Override
    public EnrollResponse updateLessonProgress(Long enrollmentId, LessonProgressRequest request) {
        // TODO: Save lesson completion to a LessonProgress table
        // For now, returns current enrollment details
        Enrollments enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("enrollment.not.found"));
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
