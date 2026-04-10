package com.example.campconnect_backend.Controllers;
import com.example.campconnect_backend.Dto.UserParticipantDto;

import com.example.campconnect_backend.Services.UserParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
 
@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserParticipantController {
 
    private final UserParticipantService service;
 
    // GET /api/participants  →  tous les participants
    @GetMapping
    public ResponseEntity<List<UserParticipantDto.Response>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // POST /api/participants  →  rejoindre un GroupMatch
    @PostMapping
    public ResponseEntity<UserParticipantDto.Response> join(
            @RequestBody UserParticipantDto.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.join(req));
    }
 
    // GET /api/participants/group/{groupMatchId}  →  liste des participants d'un group
    @GetMapping("/group/{groupMatchId}")
    public ResponseEntity<List<UserParticipantDto.Response>> getByGroup(
            @PathVariable Long groupMatchId) {
        return ResponseEntity.ok(service.getByGroupMatch(groupMatchId));
    }
 
    // GET /api/participants/user/{userId}  →  les groupes d'un user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserParticipantDto.Response>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }
 
    // PUT /api/participants/{userId}/{groupMatchId}  →  modifier rôle/statut/score
    @PutMapping("/{userId}/{groupMatchId}")
    public ResponseEntity<UserParticipantDto.Response> update(
            @PathVariable Long userId,
            @PathVariable Long groupMatchId,
            @RequestBody UserParticipantDto.UpdateRequest req) {
        return ResponseEntity.ok(service.update(userId, groupMatchId, req));
    }
 
    // DELETE /api/participants/{userId}/{groupMatchId}  →  quitter le groupe
    @DeleteMapping("/{userId}/{groupMatchId}")
    public ResponseEntity<Void> leave(
            @PathVariable Long userId,
            @PathVariable Long groupMatchId) {
        service.leave(userId, groupMatchId);
        return ResponseEntity.noContent().build();
    }
 
    // GET /api/participants/leaderboard  →  classement global
    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserParticipantDto.Response>> leaderboard() {
        return ResponseEntity.ok(service.getLeaderboard());
    }
}
 