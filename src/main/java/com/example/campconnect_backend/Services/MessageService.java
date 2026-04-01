package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.MessageDto;
import com.example.campconnect_backend.Entities.GroupMatch;
import com.example.campconnect_backend.Entities.Message;
import com.example.campconnect_backend.Entities.User;
import com.example.campconnect_backend.Repositories.GroupMatchRepository;
import com.example.campconnect_backend.Repositories.MessageRepository;
import com.example.campconnect_backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final GroupMatchRepository groupMatchRepo;

    @Transactional
    public MessageDto.Response send(MessageDto.Request req) {
        User user = userRepo.findById(req.getSenderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable"));

        GroupMatch gm = groupMatchRepo.findById(req.getGroupMatchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupMatch introuvable"));

        Message msg = Message.builder()
                .content(req.getContent())
                .sender(user)
                .groupMatch(gm)
                .build();

        return toResponse(messageRepo.save(msg));
    }

    @Transactional(readOnly = true)
    public List<MessageDto.Response> getByGroupMatch(Long groupMatchId) {
        return messageRepo.findByGroupMatchIdOrderBySentAtAsc(groupMatchId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDto.Response> getByUser(Long userId) {
        return messageRepo.findBySenderId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long messageId) {
        if (!messageRepo.existsById(messageId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable");
        messageRepo.deleteById(messageId);
    }

    private MessageDto.Response toResponse(Message m) {
        return MessageDto.Response.builder()
                .id(m.getId())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .senderId(m.getSender().getId())
                .senderUsername(m.getSender().getUsername())
                .senderAvatarUrl(m.getSender().getAvatarUrl())
                .groupMatchId(m.getGroupMatch() != null ? m.getGroupMatch().getId() : null)
                .build();
    }
}