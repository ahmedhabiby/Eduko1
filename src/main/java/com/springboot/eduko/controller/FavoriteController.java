package com.springboot.eduko.controller;

import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.EduCourses;
import com.springboot.eduko.model.Favorite;
import com.springboot.eduko.repo.BaseUserRepo;
import com.springboot.eduko.repo.CourseRepo;
import com.springboot.eduko.repo.FavoriteRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Favorites", description = "Save and manage favorite courses for the authenticated student")
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteRepo favoriteRepo;
    private final BaseUserRepo baseUserRepo;
    private final CourseRepo   courseRepo;

    @Autowired
    public FavoriteController(FavoriteRepo favoriteRepo,
                              BaseUserRepo baseUserRepo,
                              CourseRepo courseRepo) {
        this.favoriteRepo = favoriteRepo;
        this.baseUserRepo = baseUserRepo;
        this.courseRepo   = courseRepo;
    }

    private BaseUser currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return baseUserRepo.findBaseUsersByEmail(email);
    }

    private Map<String, Object> toMap(Favorite f) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",       f.getId().toString());
        m.put("courseId", f.getCourse().getId().toString());
        m.put("userId",   f.getUser().getId().toString());
        m.put("savedAt",  f.getSavedAt() != null ? f.getSavedAt().toString() : null);
        m.put("courseTitle",     f.getCourse().getCourseTitle());
        m.put("courseThumbnail", f.getCourse().getCourseThumbnail());
        return m;
    }

    // GET /favorites — list all favorites for auth student
    @Operation(summary = "List saved favorite courses")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        BaseUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        List<Map<String, Object>> favorites = favoriteRepo.findByUserId(user.getId())
                .stream().map(this::toMap).toList();
        return ResponseEntity.ok(Map.of("favorites", favorites));
    }

    // POST /favorites — add a favorite { courseId: ".." }
    @Operation(summary = "Add course to favorites")
    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody Map<String, Object> body) {
        BaseUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();

        Long courseId = Long.parseLong(body.get("courseId").toString());
        EduCourses course = courseRepo.findById(courseId).orElse(null);
        if (course == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Course not found"));

        // Idempotent — return existing if already saved
        var existing = favoriteRepo.findByUserIdAndCourseId(user.getId(), courseId);
        if (existing.isPresent())
            return ResponseEntity.ok(Map.of("favorite", toMap(existing.get())));

        Favorite fav = new Favorite();
        fav.setUser(user);
        fav.setCourse(course);
        fav.setSavedAt(LocalDateTime.now().toString());
        favoriteRepo.save(fav);
        return ResponseEntity.ok(Map.of("favorite", toMap(fav)));
    }

    // DELETE /favorites/{courseId}
    @Operation(summary = "Remove course from favorites")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable Long courseId) {
        BaseUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();

        favoriteRepo.findByUserIdAndCourseId(user.getId(), courseId)
                .ifPresent(favoriteRepo::delete);
        return ResponseEntity.ok(Map.of("removed", true));
    }
}
