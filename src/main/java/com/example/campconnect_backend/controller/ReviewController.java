package com.example.campconnect_backend.controller;

import com.example.campconnect_backend.model.Review;
import com.example.campconnect_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin
public class ReviewController {

    private final ReviewService service;

    @GetMapping
    public List<Review> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/flagged")
    public List<Review> getFlagged() { return service.getFlagged(); }

    @GetMapping("/site/{siteId}")
    public List<Review> getBySite(@PathVariable Long siteId) { return service.getBySite(siteId); }

    @GetMapping("/site/{siteId}/rating")
    public Map<String, Object> getRating(@PathVariable Long siteId) {
        return Map.of(
            "siteId", siteId,
            "average", service.getAverageRating(siteId),
            "count", service.getReviewCount(siteId)
        );
    }

    @PostMapping
    public Review create(@RequestBody Review review) { return service.create(review); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
