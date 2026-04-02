package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "group_match")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String sport;

    private int maxParticipants;

    private LocalDateTime scheduledAt;

    private String location;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "groupMatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserParticipant> participants;
}
