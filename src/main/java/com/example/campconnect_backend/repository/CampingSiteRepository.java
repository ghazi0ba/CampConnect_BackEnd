package com.example.campconnect_backend.repository;

import com.example.campconnect_backend.model.CampingSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampingSiteRepository extends JpaRepository<CampingSite, Long> {
    List<CampingSite> findByLocation(String location);
    List<CampingSite> findByPricePerNightLessThanEqual(double maxPrice);
    List<CampingSite> findByDeletedFalse();
}
