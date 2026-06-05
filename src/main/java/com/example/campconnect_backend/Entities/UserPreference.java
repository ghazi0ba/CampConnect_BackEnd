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


    @Column(columnDefinition = "TEXT")
    private String sports;


    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;


    private int ageMin;
    private int ageMax;


    private Double latitude;
    private Double longitude;
    private String city;


    @Builder.Default
    private double radiusKm = 50.0;


    @Column(columnDefinition = "TEXT")
    private String availability;


    private int groupSizeMin;
    private int groupSizeMax;


    @Enumerated(EnumType.STRING)
    private EngagementLevel engagementLevel;


    @Column(columnDefinition = "TEXT")
    private String languages;

    public enum SkillLevel    { BEGINNER, INTERMEDIATE, ADVANCED }
    public enum EngagementLevel { CASUAL, REGULAR, COMPETITIVE }
}