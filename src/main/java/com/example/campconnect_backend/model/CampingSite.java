package com.example.campconnect_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class CampingSite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    @Column(length = 2000)
    private String description;
    private double pricePerNight;
    private int capacity;
    private boolean deleted = false;
    private String imageUrl;
    private String imageAlt;
    private double latitude = 0.0;
    private double longitude = 0.0;
 
    @OneToMany(mappedBy = "campingSite", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();
 
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> amenities = new ArrayList<>();

    private int ecoScore = 0; // 0-100

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> ecoFeatures = new ArrayList<>(); // e.g. "solar", "recycling", "rainwater"
}
