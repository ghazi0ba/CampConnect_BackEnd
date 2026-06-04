package com.example.campconnect_backend.controller;

import com.example.campconnect_backend.model.CampingSite;
import com.example.campconnect_backend.service.CampingSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/camping-sites")
@RequiredArgsConstructor
@CrossOrigin
public class CampingSiteController {

    private final CampingSiteService service;

    @GetMapping
    public List<CampingSite> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public CampingSite getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/search")
    public List<CampingSite> findByLocation(@RequestParam String location) {
        return service.findByLocation(location);
    }

    @GetMapping("/available")
    public List<CampingSite> findAvailable(@RequestParam String date) {
        return service.findAvailable(date);
    }

    @PostMapping
    public CampingSite create(@RequestBody CampingSite site) { return service.create(site); }

    @PutMapping("/{id}")
    public CampingSite update(@PathVariable Long id, @RequestBody CampingSite site) {
        return service.update(id, site);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
