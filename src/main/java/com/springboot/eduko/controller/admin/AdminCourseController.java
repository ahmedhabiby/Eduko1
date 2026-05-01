package com.springboot.eduko.controller.admin;

import com.springboot.eduko.model.CourseModule;
import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Lectures;
import com.springboot.eduko.repo.CourseModuleRepo;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.LectureRepo;
import com.springboot.eduko.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin — Courses", description = "Manage courses, modules, and lessons")
@RestController
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseRepo       courseRepo;
    private final CourseModuleRepo moduleRepo;
    private final LectureRepo      lectureRepo;
    private final AuditLogService  auditLogService;

    @Autowired
    public AdminCourseController(CourseRepo courseRepo,
                                 CourseModuleRepo moduleRepo,
                                 LectureRepo lectureRepo,
                                 AuditLogService auditLogService) {
        this.courseRepo     = courseRepo;
        this.moduleRepo     = moduleRepo;
        this.lectureRepo    = lectureRepo;
        this.auditLogService = auditLogService;
    }

    private String actor() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ── helpers ───────────────────────────────────────────────────

    private Map<String, Object> toCourseMap(EduCourses c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",               c.getId().toString());
        m.put("titleEn",          c.getCourseTitle());
        m.put("titleAr",          c.getTitleAr());
        m.put("descriptionEn",    c.getDescription());
        m.put("descriptionAr",    c.getDescriptionAr());
        m.put("thumbnailUrl",     c.getThumbnailUrl());
        m.put("price",            c.getPrice());
        m.put("currency",         c.getCurrency());
        m.put("level",            c.getLevel());
        m.put("category",         c.getCategory());
        m.put("instructorName",   c.getInstructorName());
        m.put("rating",           c.getRating());
        m.put("enrolledCount",    c.getEnrolledCount());
        m.put("status",           c.getStatus());
        m.put("requiresApproval", c.getRequiresApproval());
        m.put("createdAt",        c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        return m;
    }

    private Map<String, Object> toModuleMap(CourseModule mod) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",      mod.getId().toString());
        m.put("titleEn", mod.getTitleEn());
        m.put("titleAr", mod.getTitleAr());
        m.put("order",   mod.getOrder());
        return m;
    }

    private Map<String, Object> toLessonMap(Lectures l) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",             l.getId().toString());
        m.put("titleEn",        l.getLectureTitle());
        m.put("titleAr",        l.getTitleAr());
        m.put("type",           l.getType());
        m.put("order",          l.getOrder());
        m.put("driveFileId",    l.getDriveFileId());
        m.put("lectureLink",    l.getLectureLink());
        m.put("durationMin",    l.getDurationMin());
        m.put("isFreePreview",  l.getIsFreePreview());
        return m;
    }

    // ══════════════════════ COURSES ════════════════════════

    @Operation(summary = "List all courses")
    @GetMapping
    public ResponseEntity<Map<String, Object>> listCourses() {
        var courses = courseRepo.findAll().stream().map(this::toCourseMap).toList();
        return ResponseEntity.ok(Map.of("courses", courses));
    }

    @Operation(summary = "Create course")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Map<String, Object> body) {
        EduCourses c = new EduCourses();
        applyCourseFields(c, body);
        courseRepo.save(c);
        auditLogService.log(actor(), "created_course", "Course: " + c.getCourseTitle());
        return ResponseEntity.ok(Map.of("course", toCourseMap(c)));
    }

    @Operation(summary = "Get course by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourse(@PathVariable Long id) {
        return courseRepo.findById(id)
                .map(c -> ResponseEntity.ok(Map.of("course", toCourseMap(c))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update course")
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id,
                                                            @RequestBody Map<String, Object> body) {
        return courseRepo.findById(id).map(c -> {
            applyCourseFields(c, body);
            courseRepo.save(c);
            auditLogService.log(actor(), "updated_course", "Course ID: " + id);
            return ResponseEntity.ok(Map.of("course", toCourseMap(c)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update course status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body) {
        return courseRepo.findById(id).map(c -> {
            c.setStatus(body.get("status"));
            courseRepo.save(c);
            auditLogService.log(actor(), "changed_course_status",
                    "Course ID: " + id + " → " + body.get("status"));
            return ResponseEntity.ok(Map.of("course", toCourseMap(c)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete course")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        if (!courseRepo.existsById(id)) return ResponseEntity.notFound().build();
        courseRepo.deleteById(id);
        auditLogService.log(actor(), "deleted_course", "Course ID: " + id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ══════════════════════ MODULES ════════════════════════

    @Operation(summary = "List modules in a course")
    @GetMapping("/{id}/modules")
    public ResponseEntity<Map<String, Object>> listModules(@PathVariable Long id) {
        List<CourseModule> mods = moduleRepo.findByCourseIdOrderByOrder(id);
        return ResponseEntity.ok(Map.of("modules", mods.stream().map(this::toModuleMap).toList()));
    }

    @Operation(summary = "Create module in a course")
    @PostMapping("/{id}/modules")
    public ResponseEntity<Map<String, Object>> createModule(@PathVariable Long id,
                                                            @RequestBody Map<String, Object> body) {
        return courseRepo.findById(id).map(course -> {
            CourseModule mod = new CourseModule();
            mod.setCourse(course);
            mod.setTitleEn((String) body.get("titleEn"));
            mod.setTitleAr((String) body.get("titleAr"));
            mod.setOrder(body.get("order") != null ? (Integer) body.get("order") : 1);
            moduleRepo.save(mod);
            auditLogService.log(actor(), "created_module",
                    "Module: " + mod.getTitleEn() + " in Course ID: " + id);
            return ResponseEntity.ok(Map.of("module", toModuleMap(mod)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update module")
    @PatchMapping("/{id}/modules/{mid}")
    public ResponseEntity<Map<String, Object>> updateModule(@PathVariable Long id,
                                                            @PathVariable Long mid,
                                                            @RequestBody Map<String, Object> body) {
        return moduleRepo.findById(mid).map(mod -> {
            if (body.containsKey("titleEn")) mod.setTitleEn((String) body.get("titleEn"));
            if (body.containsKey("titleAr")) mod.setTitleAr((String) body.get("titleAr"));
            if (body.containsKey("order"))   mod.setOrder((Integer) body.get("order"));
            moduleRepo.save(mod);
            auditLogService.log(actor(), "updated_module", "Module ID: " + mid);
            return ResponseEntity.ok(Map.of("module", toModuleMap(mod)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete module")
    @DeleteMapping("/{id}/modules/{mid}")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable Long id,
                                                            @PathVariable Long mid) {
        if (!moduleRepo.existsById(mid)) return ResponseEntity.notFound().build();
        moduleRepo.deleteById(mid);
        auditLogService.log(actor(), "deleted_module", "Module ID: " + mid);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ══════════════════════ LESSONS ════════════════════════

    @Operation(summary = "List lessons in a module")
    @GetMapping("/{id}/modules/{mid}/lessons")
    public ResponseEntity<Map<String, Object>> listLessons(@PathVariable Long mid) {
        var lessons = lectureRepo.findByModuleId(mid).stream().map(this::toLessonMap).toList();
        return ResponseEntity.ok(Map.of("lessons", lessons));
    }

    @Operation(summary = "Create lesson in a module")
    @PostMapping("/{id}/modules/{mid}/lessons")
    public ResponseEntity<Map<String, Object>> createLesson(@PathVariable Long id,
                                                            @PathVariable Long mid,
                                                            @RequestBody Map<String, Object> body) {
        return moduleRepo.findById(mid).map(mod -> {
            return courseRepo.findById(id).map(course -> {
                Lectures l = new Lectures();
                l.setModule(mod);
                l.setEduCourses(course);
                applyLessonFields(l, body);
                lectureRepo.save(l);
                auditLogService.log(actor(), "created_lesson",
                        "Lesson: " + l.getLectureTitle() + " in Module ID: " + mid);
                return ResponseEntity.ok(Map.of("lesson", toLessonMap(l)));
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update lesson")
    @PatchMapping("/{id}/modules/{mid}/lessons/{lid}")
    public ResponseEntity<Map<String, Object>> updateLesson(@PathVariable Long id,
                                                            @PathVariable Long mid,
                                                            @PathVariable Long lid,
                                                            @RequestBody Map<String, Object> body) {
        return lectureRepo.findById(lid).map(l -> {
            applyLessonFields(l, body);
            lectureRepo.save(l);
            auditLogService.log(actor(), "updated_lesson", "Lesson ID: " + lid);
            return ResponseEntity.ok(Map.of("lesson", toLessonMap(l)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete lesson")
    @DeleteMapping("/{id}/modules/{mid}/lessons/{lid}")
    public ResponseEntity<Map<String, Object>> deleteLesson(@PathVariable Long id,
                                                            @PathVariable Long mid,
                                                            @PathVariable Long lid) {
        if (!lectureRepo.existsById(lid)) return ResponseEntity.notFound().build();
        lectureRepo.deleteById(lid);
        auditLogService.log(actor(), "deleted_lesson", "Lesson ID: " + lid);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── field applicators ─────────────────────────────────────────────

    private void applyCourseFields(EduCourses c, Map<String, Object> body) {
        if (body.containsKey("titleEn"))          c.setCourseTitle((String) body.get("titleEn"));
        if (body.containsKey("titleAr"))          c.setTitleAr((String) body.get("titleAr"));
        if (body.containsKey("descriptionEn"))    c.setDescription((String) body.get("descriptionEn"));
        if (body.containsKey("descriptionAr"))    c.setDescriptionAr((String) body.get("descriptionAr"));
        if (body.containsKey("thumbnailUrl"))     c.setThumbnailUrl((String) body.get("thumbnailUrl"));
        if (body.containsKey("price"))            c.setPrice(((Number) body.get("price")).doubleValue());
        if (body.containsKey("currency"))         c.setCurrency((String) body.get("currency"));
        if (body.containsKey("level"))            c.setLevel((String) body.get("level"));
        if (body.containsKey("category"))         c.setCategory((String) body.get("category"));
        if (body.containsKey("instructorName"))   c.setInstructorName((String) body.get("instructorName"));
        if (body.containsKey("status"))           c.setStatus((String) body.get("status"));
        if (body.containsKey("requiresApproval")) c.setRequiresApproval((Boolean) body.get("requiresApproval"));
    }

    private void applyLessonFields(Lectures l, Map<String, Object> body) {
        if (body.containsKey("titleEn"))       l.setLectureTitle((String) body.get("titleEn"));
        if (body.containsKey("titleAr"))       l.setTitleAr((String) body.get("titleAr"));
        if (body.containsKey("type"))          l.setType((String) body.get("type"));
        if (body.containsKey("order"))         l.setOrder((Integer) body.get("order"));
        if (body.containsKey("driveFileId"))   l.setDriveFileId((String) body.get("driveFileId"));
        if (body.containsKey("lectureLink"))   l.setLectureLink((String) body.get("lectureLink"));
        if (body.containsKey("durationMin"))   l.setDurationMin((Integer) body.get("durationMin"));
        if (body.containsKey("isFreePreview")) l.setIsFreePreview((Boolean) body.get("isFreePreview"));
    }
}
