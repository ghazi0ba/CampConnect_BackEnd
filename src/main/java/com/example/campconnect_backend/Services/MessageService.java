package com.example.campconnect_backend.Services;


import com.example.campconnect_backend.Dto.MessageDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository    messageRepo;
    private final UserRepository       userRepo;
    private final GroupMatchRepository groupMatchRepo;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Envoyer un message privé via WebSocket ───────────────────────────────

    @Transactional
    public MessageDto.Response sendPrivate(MessageDto.WsPayload payload) {
        User sender = findUser(payload.getSenderId());
        User receiver = findUser(payload.getReceiverId());

        Message msg = Message.builder()
                .content(payload.getContent())
                .sender(sender)
                .receiver(receiver)
                .build();

        Message saved = messageRepo.save(msg);
        MessageDto.Response response = toResponse(saved);

        // Push WebSocket au destinataire
        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(),
                "/queue/messages",
                response
        );
        return response;
    }

    // ── Envoyer un message dans un groupe ────────────────────────────────────

    @Transactional
    public MessageDto.Response sendToGroup(MessageDto.WsPayload payload) {
        User sender = findUser(payload.getSenderId());
        GroupMatch gm = groupMatchRepo.findById(payload.getGroupMatchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe introuvable"));

        Message msg = Message.builder()
                .content(payload.getContent())
                .sender(sender)
                .groupMatch(gm)
                .build();

        Message saved = messageRepo.save(msg);
        MessageDto.Response response = toResponse(saved);

        // Broadcast au topic du groupe
        messagingTemplate.convertAndSend(
                "/topic/group/" + gm.getId(),
                response
        );
        return response;
    }

    // ── Historique conversation privée ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MessageDto.Response> getConversation(Long userId1, Long userId2) {
        return messageRepo.findConversation(userId1, userId2)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Historique messages d'un groupe ──────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MessageDto.Response> getGroupMessages(Long groupMatchId) {
        return messageRepo.findByGroupMatchIdOrderBySentAtAsc(groupMatchId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Messages non lus ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MessageDto.Response> getUnread(Long userId) {
        return messageRepo.findByReceiverIdAndIsReadFalse(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Dernières conversations ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MessageDto.Response> getLastConversations(Long userId) {
        return messageRepo.findLastMessagesForUser(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Marquer comme lu ─────────────────────────────────────────────────────

    @Transactional
    public void markAsRead(Long senderId, Long receiverId) {
        messageRepo.markAsRead(senderId, receiverId);
    }

    private User findUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable"));
    }

    private MessageDto.Response toResponse(Message m) {
        return MessageDto.Response.builder()
                .id(m.getId())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .senderId(m.getSender().getId())
                .senderUsername(m.getSender().getUsername())
                .senderAvatarUrl(m.getSender().getAvatarUrl())
                .receiverId(m.getReceiver() != null ? m.getReceiver().getId() : null)
                .receiverUsername(m.getReceiver() != null ? m.getReceiver().getUsername() : null)
                .groupMatchId(m.getGroupMatch() != null ? m.getGroupMatch().getId() : null)
                .isRead(m.isRead())
                .build();
    }
}