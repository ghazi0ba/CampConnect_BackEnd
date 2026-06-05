package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "user_participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserParticipant {
 
    @EmbeddedId
    private UserParticipantId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupMatchId")
    @JoinColumn(name = "group_match_id")
    private GroupMatch groupMatch;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantRole role;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status;
 
    @Column(nullable = false)
    private LocalDateTime joinedAt;
 
    @Column(nullable = false)
    private int score;
 
    @PrePersist
    public void prePersist() {
        if (this.joinedAt == null) this.joinedAt = LocalDateTime.now();
        if (this.role == null) this.role = ParticipantRole.MEMBER;
        if (this.status == null) this.status = ParticipantStatus.PENDING;
    }
}