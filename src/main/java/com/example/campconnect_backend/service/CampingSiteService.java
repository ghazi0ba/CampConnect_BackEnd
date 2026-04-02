package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.CampingSite;
import com.example.campconnect_backend.repository.CampingSiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampingSiteService {

    private final CampingSiteRepository repository;

    public List<CampingSite> getAll() {
        return repository.findByDeletedFalse();
    }

    public CampingSite getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("CampingSite not found"));
    }

    public CampingSite create(CampingSite site) { return repository.save(site); }

    public CampingSite update(Long id, CampingSite updated) {
        CampingSite site = getById(id);
        site.setName(updated.getName());
        site.setLocation(updated.getLocation());
        site.setDescription(updated.getDescription());
        site.setPricePerNight(updated.getPricePerNight());
        site.setCapacity(updated.getCapacity());
        return repository.save(site);
    }

    public void delete(Long id) {
        CampingSite site = getById(id);
        site.setDeleted(true);
        repository.save(site);
    }

    public List<CampingSite> findByLocation(String location) { return repository.findByLocation(location); }
}
