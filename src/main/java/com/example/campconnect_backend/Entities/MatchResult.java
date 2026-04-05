package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_results")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_group_id", nullable = false)
    public GroupMatch matchedGroup;

    // Score global de compatibilité (0-100)
    private double compatibilityScore;

    // Détail des scores par critère
    private double sportScore;
    private double locationScore;
    private double skillScore;
    private double availabilityScore;
    private double groupSizeScore;

    // Distance en km
    private double distanceKm;

    private LocalDateTime computedAt;

    @PrePersist
    public void prePersist() { this.computedAt = LocalDateTime.now(); }
}