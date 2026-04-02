package com.example.campconnect_backend.Entities;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipantId implements Serializable {
    private Long userId;
    private Long groupMatchId;
}