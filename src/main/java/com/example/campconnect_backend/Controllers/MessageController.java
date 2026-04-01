package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.MessageDto;
import com.example.campconnect_backend.Services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private final MessageService service;

    @PostMapping
    public ResponseEntity<MessageDto.Response> send(@RequestBody MessageDto.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.send(req));
    }

    @GetMapping("/group/{groupMatchId}")
    public ResponseEntity<List<MessageDto.Response>> getByGroup(@PathVariable Long groupMatchId) {
        return ResponseEntity.ok(service.getByGroupMatch(groupMatchId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDto.Response>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}