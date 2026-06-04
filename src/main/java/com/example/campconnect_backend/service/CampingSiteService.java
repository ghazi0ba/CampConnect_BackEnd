package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.CampingSite;
import com.example.campconnect_backend.repository.CampingSiteRepository;
import com.example.campconnect_backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CampingSiteService {

    private final CampingSiteRepository repository;
    private final ReservationRepository reservationRepository;

    public List<CampingSite> getAll() {
        return repository.findByDeletedFalse();
    }

    public CampingSite getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("CampingSite not found"));
    }

    public CampingSite create(CampingSite site) {
        if (site.getImages() != null) {
            site.getImages().forEach(img -> img.setCampingSite(site));
        }
        return repository.save(site);
    }

    public CampingSite update(Long id, CampingSite updated) {
        CampingSite site = getById(id);
        site.setName(updated.getName());
        site.setLocation(updated.getLocation());
        site.setDescription(updated.getDescription());
        site.setPricePerNight(updated.getPricePerNight());
        site.setCapacity(updated.getCapacity());
        site.setImageUrl(updated.getImageUrl());
        site.setImageAlt(updated.getImageAlt());
        site.setLatitude(updated.getLatitude());
        site.setLongitude(updated.getLongitude());
        site.setEcoScore(updated.getEcoScore());

        if (updated.getAmenities() != null) {
            site.getAmenities().clear();
            site.getAmenities().addAll(updated.getAmenities());
        }

        if (updated.getEcoFeatures() != null) {
            site.getEcoFeatures().clear();
            site.getEcoFeatures().addAll(updated.getEcoFeatures());
        }

        if (updated.getImages() != null) {
            site.getImages().clear();
            for (com.example.campconnect_backend.model.Image newImg : updated.getImages()) {
                newImg.setCampingSite(site);
                site.getImages().add(newImg);
            }
        }

        CampingSite saved = repository.save(site);
        repository.flush();
        return saved;
    }

    public void delete(Long id) {
        CampingSite site = getById(id);
        site.setDeleted(true);
        repository.save(site);
    }

    public List<CampingSite> findByLocation(String location) { return repository.findByLocationContainingIgnoreCase(location); }

    public List<CampingSite> findAvailable(String dateStr) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            // Get all site ids that are booked on this date
            Set<Long> bookedIds = reservationRepository.findAll().stream()
                    .filter(r -> !"CANCELLED".equals(r.getStatus()))
                    .filter(r -> !r.getStartDate().after(date) && !r.getEndDate().before(date))
                    .map(r -> r.getCampingSite().getId())
                    .collect(Collectors.toSet());
            return repository.findByDeletedFalse().stream()
                    .filter(s -> !bookedIds.contains(s.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return repository.findByDeletedFalse();
        }
    }
}
