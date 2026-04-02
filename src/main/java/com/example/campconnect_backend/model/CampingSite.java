package com.example.campconnect_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CampingSite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String description;
    private double pricePerNight;
    private int capacity;
    private boolean deleted = false;
}
