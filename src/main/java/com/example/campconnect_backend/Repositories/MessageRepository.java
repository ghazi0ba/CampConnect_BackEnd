package com.example.campconnect_backend.Repositories;




import com.example.campconnect_backend.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Conversation privée entre 2 users (dans les 2 sens)
    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender.id = :u1 AND m.receiver.id = :u2)
           OR (m.sender.id = :u2 AND m.receiver.id = :u1)
        ORDER BY m.sentAt ASC
        """)
    List<Message> findConversation(@Param("u1") Long userId1, @Param("u2") Long userId2);

    // Messages d'un groupe
    List<Message> findByGroupMatchIdOrderBySentAtAsc(Long groupMatchId);

    // Derniers messages reçus non lus
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);

    // Toutes les conversations d'un user (derniers messages)
    @Query("""
        SELECT m FROM Message m
        WHERE m.id IN (
            SELECT MAX(m2.id) FROM Message m2
            WHERE m2.sender.id = :uid OR m2.receiver.id = :uid
            GROUP BY CASE
                WHEN m2.sender.id = :uid THEN m2.receiver.id
                ELSE m2.sender.id
            END
        )
        ORDER BY m.sentAt DESC
        """)
    List<Message> findLastMessagesForUser(@Param("uid") Long userId);

    // Marquer messages comme lus
    @Query("UPDATE Message m SET m.isRead = true WHERE m.sender.id = :senderId AND m.receiver.id = :receiverId AND m.isRead = false")
    void markAsRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}