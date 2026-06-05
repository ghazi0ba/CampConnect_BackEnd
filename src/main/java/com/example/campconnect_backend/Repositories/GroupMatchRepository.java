package com.example.campconnect_backend.Repositories;

import com.example.campconnect_backend.Entities.GroupMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupMatchRepository extends JpaRepository<GroupMatch, Long> {
    List<GroupMatch> findBySport(String sport);
    List<GroupMatch> findByNameContainingIgnoreCase(String name);
}