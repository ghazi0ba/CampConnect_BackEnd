package com.example.campconnect_backend.controller;

import com.example.campconnect_backend.model.Reservation;
import com.example.campconnect_backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @PostMapping
    public Reservation create(@RequestBody Reservation reservation) { return service.create(reservation); }

    @PutMapping("/{id}")
    public Reservation update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return service.update(id, reservation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PutMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id) { return service.cancel(id); }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
