package com.example.campconnect_backend.Repositories;



import com.example.campconnect_backend.Entities.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    List<MatchResult> findByUserIdOrderByCompatibilityScoreDesc(Long userId);
    void deleteByUserId(Long userId);
}