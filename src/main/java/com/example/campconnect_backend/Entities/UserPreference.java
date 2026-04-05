package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "user_preferences")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Sports préférés (ex: "football,tennis,randonnée")
    @Column(columnDefinition = "TEXT")
    private String sports;

    // Niveau de compétence : BEGINNER, INTERMEDIATE, ADVANCED
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;

    // Tranche d'âge préférée pour les partenaires
    private int ageMin;
    private int ageMax;

    // Localisation GPS
    private Double latitude;
    private Double longitude;
    private String city;

    // Rayon de recherche en km
    @Builder.Default
    private double radiusKm = 50.0;

    // Disponibilité : MORNING, AFTERNOON, EVENING, WEEKEND
    @Column(columnDefinition = "TEXT")
    private String availability;

    // Taille de groupe préférée
    private int groupSizeMin;
    private int groupSizeMax;

    // Niveau d'engagement : CASUAL, REGULAR, COMPETITIVE
    @Enumerated(EnumType.STRING)
    private EngagementLevel engagementLevel;

    // Langues parlées
    @Column(columnDefinition = "TEXT")
    private String languages;

    public enum SkillLevel    { BEGINNER, INTERMEDIATE, ADVANCED }
    public enum EngagementLevel { CASUAL, REGULAR, COMPETITIVE }
}