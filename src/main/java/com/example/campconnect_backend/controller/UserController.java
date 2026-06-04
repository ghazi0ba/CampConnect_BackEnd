package com.example.campconnect_backend.controller;

import com.example.campconnect_backend.model.CampingSite;
import com.example.campconnect_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}/favorites")
    public Set<CampingSite> getFavorites(@PathVariable Long id) {
        return userService.getFavorites(id);
    }

    @PostMapping("/{id}/favorites/{siteId}")
    public void toggleFavorite(@PathVariable Long id, @PathVariable Long siteId) {
        userService.toggleFavorite(id, siteId);
    }
}
