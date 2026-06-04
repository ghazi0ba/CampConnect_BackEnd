package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.CampingSite;
import com.example.campconnect_backend.model.Reservation;
import com.example.campconnect_backend.model.User;
import com.example.campconnect_backend.repository.CampingSiteRepository;
import com.example.campconnect_backend.repository.ReservationRepository;
import com.example.campconnect_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;
    private final UserRepository userRepository;
    private final CampingSiteRepository campingSiteRepository;
    private final EmailService emailService;

    public List<Reservation> getAll() {
        autoCancelExpired();
        return repository.findAll();
    }

    public Reservation getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public Reservation create(Reservation reservation) {
        Long userId = reservation.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Long siteId = reservation.getCampingSite().getId();
        CampingSite site = campingSiteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Camping site not found with id: " + siteId));

        reservation.setUser(user);
        reservation.setCampingSite(site);

        int requestedGuests = reservation.getGuests() > 0 ? reservation.getGuests() : 1;
        int bookedGuests = repository.countGuestsOverlap(siteId, reservation.getStartDate(), reservation.getEndDate());
        int availableSpots = site.getCapacity() - bookedGuests;

        if (availableSpots <= 0) throw new RuntimeException("Site is fully booked for the selected dates.");
        if (requestedGuests > availableSpots) throw new RuntimeException("Only " + availableSpots + " spot(s) available.");

        // Calculate total price
        long diffMs = reservation.getEndDate().getTime() - reservation.getStartDate().getTime();
        long nights = Math.max(1, TimeUnit.MILLISECONDS.toDays(diffMs));
        double total = site.getPricePerNight() * nights + 12 + 25; // permit + cleaning

        reservation.setGuests(requestedGuests);
        reservation.setTotalPrice(total);
        reservation.setStatus("PENDING");
        reservation.setPaymentStatus("UNPAID");
        reservation.setPaymentDeadline(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)));

        Reservation saved = repository.save(reservation);

        // Send payment reminder email
        emailService.sendPaymentReminder(saved);
        return saved;
    }

    public Reservation pay(Long id) {
        Reservation reservation = getById(id);
        if ("CANCELLED".equals(reservation.getStatus())) throw new RuntimeException("Reservation is cancelled.");
        if ("PAID".equals(reservation.getPaymentStatus())) throw new RuntimeException("Already paid.");

        reservation.setPaymentStatus("PAID");
        reservation.setStatus("CONFIRMED");
        Reservation saved = repository.save(reservation);
        emailService.sendReservationConfirmation(saved);
        return saved;
    }

    // Runs every 5 minutes — auto-cancel unpaid reservations past deadline
    @Scheduled( cron = "* */5 ")
    public void autoCancelExpired() {
        // Cancel PENDING+UNPAID where deadline has passed
        List<Reservation> expired = repository.findByStatusAndPaymentStatusAndPaymentDeadlineBefore(
                "PENDING", "UNPAID", new Date());
        for (Reservation r : expired) {
            r.setStatus("CANCELLED");
            r.setCancellationReason("PAYMENT_EXPIRED");
            repository.save(r);
            try { emailService.sendPaymentExpiredNotification(r); } catch (Exception ignored) {}
            System.out.println("Auto-cancelled reservation #" + r.getId() + " — payment deadline passed.");
        }

        // Cancel PENDING where check-in date (startDate) has passed
        List<Reservation> pastCheckIn = repository.findByStatusAndStartDateBefore("PENDING", new Date());
        for (Reservation r : pastCheckIn) {
            r.setStatus("CANCELLED");
            r.setCancellationReason("CHECKIN_DATE_PASSED");
            repository.save(r);
            System.out.println("Auto-cancelled pending reservation #" + r.getId() + " — check-in date has passed.");
        }

        // Also cancel PENDING reservations with null deadline (legacy data)
        repository.findAll().stream()
                .filter(r -> "PENDING".equals(r.getStatus()) && r.getPaymentDeadline() == null)
                .forEach(r -> {
                    r.setStatus("CANCELLED");
                    r.setCancellationReason("PAYMENT_EXPIRED");
                    repository.save(r);
                    System.out.println("Auto-cancelled legacy pending reservation #" + r.getId());
                });
    }

    public Reservation cancel(Long id) {
        Reservation reservation = getById(id);
        if ("CANCELLED".equals(reservation.getStatus())) throw new RuntimeException("Already cancelled.");
        long diff = reservation.getStartDate().getTime() - new Date().getTime();
        long hoursUntilStart = TimeUnit.MILLISECONDS.toHours(diff);
        if ("CONFIRMED".equals(reservation.getStatus()) && hoursUntilStart < 48)
            throw new RuntimeException("Cannot cancel less than 48 hours before the start date.");
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
    public List<Reservation> getByUser(Long userId) {
        autoCancelExpired();
        return repository.findByUserId(userId);
    }
    public List<Reservation> getBySite(Long siteId) { return repository.findByCampingSiteId(siteId); }

    public Map<String, Integer> getAvailabilityByDate(Long siteId, String from, String to) {
        try {
            CampingSite site = campingSiteRepository.findById(siteId).orElseThrow();
            int capacity = site.getCapacity();
            java.time.LocalDate start = java.time.LocalDate.parse(from);
            java.time.LocalDate end = java.time.LocalDate.parse(to);
            Map<String, Integer> result = new LinkedHashMap<>();
            for (java.time.LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                Date date = Date.from(d.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                Date datePlusOne = Date.from(d.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                int booked = repository.countGuestsOverlap(siteId, date, datePlusOne);
                result.put(d.toString(), Math.max(0, capacity - booked));
            }
            return result;
        } catch (Exception e) { return new HashMap<>(); }
    }
}
