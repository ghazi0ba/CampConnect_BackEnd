package com.example.campconnect_backend.Controllers;



import com.example.campconnect_backend.Dto.*;
import com.example.campconnect_backend.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AiMatchingController {

    private final AiMatchingService       matchingService;
    private final UserPreferenceService   prefService;

    // POST /api/matching/preferences  →  sauvegarder préférences
    @PostMapping("/preferences")
    public ResponseEntity<UserPreferenceDto.Response> savePreferences(
            @RequestBody UserPreferenceDto.Request req) {
        return ResponseEntity.ok(prefService.save(req));
    }

    // GET /api/matching/preferences/{userId}  →  récupérer préférences
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<UserPreferenceDto.Response> getPreferences(
            @PathVariable Long userId) {
        return ResponseEntity.ok(prefService.getByUser(userId));
    }

    // GET /api/matching/compute/{userId}  →  lancer le matching IA
    @GetMapping("/compute/{userId}")
    public ResponseEntity<List<MatchResultDto.Response>> computeMatches(
            @PathVariable Long userId) {
        return ResponseEntity.ok(matchingService.computeMatches(userId));
    }

    // POST /api/matching/ai/chat  →  chat avec l'IA
    @PostMapping("/ai/chat")
    public ResponseEntity<AiChatDto.Response> chatWithAI(
            @RequestBody AiChatDto.Request req) {
        String response = matchingService.chatWithAI(req.getMessage());
        return ResponseEntity.ok(new AiChatDto.Response(response));
    }
}