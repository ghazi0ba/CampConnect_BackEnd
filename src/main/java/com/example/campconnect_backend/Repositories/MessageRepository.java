package com.example.campconnect_backend.Repositories;


import com.example.campconnect_backend.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderId(Long senderId);
    List<Message> findByGroupMatchId(Long groupMatchId);
    List<Message> findByGroupMatchIdOrderBySentAtAsc(Long groupMatchId);
}
