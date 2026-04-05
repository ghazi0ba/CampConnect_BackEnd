package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.MessageDto;
import com.example.campconnect_backend.Services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private final MessageService service;

    // ── WebSocket handlers ───────────────────────────────────────────────────

    // Client envoie : /app/chat.private
    @MessageMapping("/chat.private")
    public void handlePrivate(MessageDto.WsPayload payload) {
        service.sendPrivate(payload);
    }

    // Client envoie : /app/chat.group
    @MessageMapping("/chat.group")
    public void handleGroup(MessageDto.WsPayload payload) {
        service.sendToGroup(payload);
    }

    // ── REST endpoints ───────────────────────────────────────────────────────

    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<List<MessageDto.Response>> getConversation(
            @PathVariable Long userId1, @PathVariable Long userId2) {
        return ResponseEntity.ok(service.getConversation(userId1, userId2));
    }

    @GetMapping("/group/{groupMatchId}")
    public ResponseEntity<List<MessageDto.Response>> getGroupMessages(
            @PathVariable Long groupMatchId) {
        return ResponseEntity.ok(service.getGroupMessages(groupMatchId));
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<MessageDto.Response>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnread(userId));
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<MessageDto.Response>> getLastConversations(
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getLastConversations(userId));
    }

    @PutMapping("/read/{senderId}/{receiverId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long senderId, @PathVariable Long receiverId) {
        service.markAsRead(senderId, receiverId);
        return ResponseEntity.ok().build();
    }
}