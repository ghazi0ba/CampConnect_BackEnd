package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.UserDto;
import com.example.campconnect_backend.Dto.UserPreferenceDto;
import com.example.campconnect_backend.Services.UserPreferenceService;
import com.example.campconnect_backend.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService service;
    private final UserPreferenceService upservice;

    @PostMapping
    public ResponseEntity<UserDto.Response> create(@RequestBody UserDto.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<UserDto.Response>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto.Response> update(@PathVariable Long id,
                                                   @RequestBody UserDto.UpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{userId}/preferences")
    public ResponseEntity<UserPreferenceDto.Response> savePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferenceDto.Request req) {
        req.setUserId(userId);
        UserPreferenceDto.Response response = upservice.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}