package com.example.campconnect_backend.repository;

import com.example.campconnect_backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByCampingSiteId(Long campingSiteId);
    List<Reservation> findByStatusAndPaymentStatusAndPaymentDeadlineBefore(
            String status, String paymentStatus, Date deadline);
    List<Reservation> findByStatusAndStartDateBefore(String status, Date date);

    @Query("SELECT COALESCE(SUM(r.guests), 0) FROM Reservation r WHERE r.campingSite.id = :siteId " +
            "AND r.status != 'CANCELLED' " +
            "AND r.startDate < :endDate AND r.endDate > :startDate")
    int countGuestsOverlap(@Param("siteId") Long siteId,
                           @Param("startDate") Date startDate,
                           @Param("endDate") Date endDate);
}
