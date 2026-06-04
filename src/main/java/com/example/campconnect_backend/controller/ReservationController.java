package com.example.campconnect_backend.controller;

import com.example.campconnect_backend.model.Reservation;
import com.example.campconnect_backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin
public class ReservationController {

    private final ReservationService service;

    @GetMapping
    public List<Reservation> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/user/{userId}")
    public List<Reservation> getByUser(@PathVariable Long userId) { return service.getByUser(userId); }

    @GetMapping("/site/{siteId}")
    public List<Reservation> getBySite(@PathVariable Long siteId) { return service.getBySite(siteId); }

    @GetMapping("/site/{siteId}/availability")
    public Map<String, Integer> getAvailability(
            @PathVariable Long siteId,
            @RequestParam String from,
            @RequestParam String to) {
        return service.getAvailabilityByDate(siteId, from, to);
    }

    @PostMapping
    public Reservation create(@RequestBody Reservation reservation) { return service.create(reservation); }

    @PostMapping("/{id}/pay")
    public Reservation pay(@PathVariable Long id) { return service.pay(id); }

    @PostMapping("/admin/cancel-expired")
    public String cancelExpired() {
        service.autoCancelExpired();
        return "Expired reservations processed.";
    }

    @PutMapping("/{id}")
    public Reservation update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return service.update(id, reservation);
    }

    @PutMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id) { return service.cancel(id); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
