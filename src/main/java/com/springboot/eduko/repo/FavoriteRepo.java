package com.springboot.eduko.repo;

import com.springboot.eduko.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepo extends JpaRepository<Favorite, Long> {

    /** All favorites for a given user (for GET /favorites) */
    List<Favorite> findByUserId(Long userId);

    /** Idempotency check — does this user already have this course saved? */
    Optional<Favorite> findByUserIdAndCourseId(Long userId, Long courseId);

    /** Used when student un-favorites — delete by user + course */
    void deleteByUserIdAndCourseId(Long userId, Long courseId);
}
