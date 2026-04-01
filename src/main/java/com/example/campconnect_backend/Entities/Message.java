package com.example.campconnect_backend.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
 
    @Column(nullable = false)
    private LocalDateTime sentAt;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_match_id")
    private GroupMatch groupMatch;
 
    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
    }
}