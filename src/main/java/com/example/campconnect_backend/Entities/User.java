package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Unified user model, aligned with the main CampConnect backend
 * (firstName/lastName/email/password/role enum/phone + orders), extended with
 * this branch's matching/messaging fields (username, avatarUrl, messages,
 * participations).
 *
 * Note: firstName/lastName are optional here because this branch creates users
 * from a username only; role defaults to USER via the builder so the existing
 * user-creation flow keeps working unchanged.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    private String phone;

    // --- Branch (matching / messaging) profile fields ---

    @Column(unique = true)
    private String username;

    private String avatarUrl;

    // --- Relations ---

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserParticipant> participations;
}
