package com.example.campconnect_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startDate;
    private Date endDate;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private int guests = 1;
    private double totalPrice;
    private Date paymentDeadline;
    private String paymentStatus; // UNPAID, PAID
    private String cancellationReason; // PAYMENT_EXPIRED, USER_CANCELLED, ADMIN_CANCELLED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "camping_site_id")
    private CampingSite campingSite;
}
