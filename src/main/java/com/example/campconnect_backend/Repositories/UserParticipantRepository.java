package com.example.campconnect_backend.Repositories;
 

import com.example.campconnect_backend.Entities.ParticipantStatus;
import com.example.campconnect_backend.Entities.UserParticipant;
import com.example.campconnect_backend.Entities.UserParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
 
// ── UserParticipant Repository ───────────────────────────────────────────────
 
@Repository
public interface UserParticipantRepository
        extends JpaRepository<UserParticipant, UserParticipantId> {
 
    List<UserParticipant> findByUserId(Long userId);
 
    List<UserParticipant> findByGroupMatchId(Long groupMatchId);
 
    List<UserParticipant> findByGroupMatchIdAndStatus(Long groupMatchId, ParticipantStatus status);
 
    Optional<UserParticipant> findByUserIdAndGroupMatchId(Long userId, Long groupMatchId);
 
    boolean existsByUserIdAndGroupMatchId(Long userId, Long groupMatchId);
 
    @Query("SELECT COUNT(up) FROM UserParticipant up WHERE up.groupMatch.id = :gid AND up.status = 'ACCEPTED'")
    long countAcceptedByGroupMatchId(@Param("gid") Long groupMatchId);
 
    @Query("SELECT up FROM UserParticipant up ORDER BY up.score DESC")
    List<UserParticipant> findLeaderboard();
}