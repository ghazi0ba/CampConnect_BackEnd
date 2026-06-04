package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.Review;
import com.example.campconnect_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;
    private final EmailService emailService;

    // Keywords that indicate a safety concern
    private static final Map<String, String> SAFETY_KEYWORDS = Map.ofEntries(
        Map.entry("fire", "Fire hazard reported"),
        Map.entry("flood", "Flooding reported"),
        Map.entry("snake", "Wildlife danger reported"),
        Map.entry("bear", "Wildlife danger reported"),
        Map.entry("scorpion", "Wildlife danger reported"),
        Map.entry("dangerous", "Safety concern reported"),
        Map.entry("unsafe", "Safety concern reported"),
        Map.entry("hazard", "Hazard reported"),
        Map.entry("accident", "Accident reported"),
        Map.entry("injured", "Injury reported"),
        Map.entry("injury", "Injury reported"),
        Map.entry("emergency", "Emergency situation reported"),
        Map.entry("broken", "Infrastructure issue reported"),
        Map.entry("collapsed", "Structural issue reported"),
        Map.entry("toxic", "Toxic hazard reported"),
        Map.entry("contaminated", "Contamination reported")
    );

    public List<Review> getAll() {
        return repository.findByDeletedFalse();
    }

    public List<Review> getFlagged() {
        return repository.findByFlaggedTrueAndDeletedFalse();
    }

    public Review getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public Review create(Review review) {
        review.setCreatedAt(new Date());
        analyzeForSafety(review);
        Review saved = repository.save(review);
        if (saved.isFlagged()) {
            emailService.sendSafetyAlert(saved);
        }
        return saved;
    }

    public void delete(Long id) {
        Review review = getById(id);
        review.setDeleted(true);
        repository.save(review);
    }

    public List<Review> getBySite(Long siteId) {
        return repository.findByCampingSiteId(siteId);
    }

    public double getAverageRating(Long siteId) {
        List<Review> reviews = repository.findByCampingSiteId(siteId);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public long getReviewCount(Long siteId) {
        return repository.findByCampingSiteId(siteId).size();
    }

    private void analyzeForSafety(Review review) {
        String text = review.getComment().toLowerCase();

        // Flag if rating is 1 star
        if (review.getRating() == 1) {
            review.setFlagged(true);
            review.setFlagReason("1-star rating — requires review");
            return;
        }

        // Check for safety keywords
        for (Map.Entry<String, String> entry : SAFETY_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey())) {
                review.setFlagged(true);
                review.setFlagReason(entry.getValue());
                return;
            }
        }
    }
}
