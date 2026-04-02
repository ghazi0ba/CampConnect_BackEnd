package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.Review;
import com.example.campconnect_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;

    public List<Review> getAll() {
        return repository.findByDeletedFalse();
    }

    public Review getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public Review create(Review review) { return repository.save(review); }

    public void delete(Long id) {
        Review review = getById(id);
        review.setDeleted(true);
        repository.save(review);
    }

    public List<Review> getBySite(Long siteId) { return repository.findByCampingSiteId(siteId); }
}
