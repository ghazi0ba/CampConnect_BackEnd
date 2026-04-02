package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.Reservation;
import com.example.campconnect_backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;

    public List<Reservation> getAll() { return repository.findAll(); }

    public Reservation getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public Reservation create(Reservation reservation) {
        boolean overlap = repository.existsOverlap(
                reservation.getCampingSite().getId(),
                reservation.getStartDate(),
                reservation.getEndDate()
        );
        if (overlap) {
            throw new RuntimeException("Site is already booked for the selected dates");
        }
        reservation.setStatus("CONFIRMED");
        return repository.save(reservation);
    }

    public Reservation cancel(Long id) {
        Reservation reservation = getById(id);
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new RuntimeException("Reservation is already cancelled");
        }
        long diff = reservation.getStartDate().getTime() - new Date().getTime();
        long hoursUntilStart = TimeUnit.MILLISECONDS.toHours(diff);
        if (hoursUntilStart < 48) {
            throw new RuntimeException("Cannot cancel less than 48 hours before the start date");
        }
        reservation.setStatus("CANCELLED");
        return repository.save(reservation);
    }

    public Reservation update(Long id, Reservation updated) {
        Reservation reservation = getById(id);
        reservation.setStartDate(updated.getStartDate());
        reservation.setEndDate(updated.getEndDate());
        reservation.setStatus(updated.getStatus());
        return repository.save(reservation);
    }

    public void delete(Long id) { repository.deleteById(id); }

    public List<Reservation> getByUser(Long userId) { return repository.findByUserId(userId); }

    public List<Reservation> getBySite(Long siteId) { return repository.findByCampingSiteId(siteId); }
}
