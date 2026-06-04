package com.example.campconnect_backend.repository;

import com.example.campconnect_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCampingSiteId(Long campingSiteId);
    List<Review> findByUserId(Long userId);
    List<Review> findByDeletedFalse();
    List<Review> findByFlaggedTrueAndDeletedFalse();
}
