package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.UserPreferenceDto;
import com.example.campconnect_backend.Services.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserPreferenceController {

    private final UserPreferenceService service;

    /**
     * Save or update user preferences
     * POST /api/users/{userId}/preferences
     */
    @PostMapping("/{userId}/preferences")
    public ResponseEntity<UserPreferenceDto.Response> savePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferenceDto.Request req) {
        req.setUserId(userId);
        UserPreferenceDto.Response response = service.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get preferences for a user
     * GET /api/users/{userId}/preferences
     */
    @GetMapping("/{userId}/preferences")
    public ResponseEntity<UserPreferenceDto.Response> getPreferences(@PathVariable Long userId) {
        UserPreferenceDto.Response response = service.getByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user location
     * POST /api/users/{userId}/location
     */
    @PostMapping("/{userId}/location")
    public ResponseEntity<Map<String, Object>> updateLocation(
            @PathVariable Long userId,
            @RequestBody Map<String, Double> locationData) {
        Double latitude = locationData.get("latitude");
        Double longitude = locationData.get("longitude");

        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Latitude and longitude are required"
            ));
        }

        // Get existing preferences
        UserPreferenceDto.Response prefs = service.getByUser(userId);

        // Update with new location
        UserPreferenceDto.Request updateReq = UserPreferenceDto.Request.builder()
                .userId(userId)
                .latitude(latitude)
                .longitude(longitude)
                .sports(prefs.getSports())
                .skillLevel(prefs.getSkillLevel())
                .ageMin(prefs.getAgeMin())
                .ageMax(prefs.getAgeMax())
                .city(prefs.getCity())
                .radiusKm(prefs.getRadiusKm())
                .availability(prefs.getAvailability())
                .groupSizeMin(prefs.getGroupSizeMin())
                .groupSizeMax(prefs.getGroupSizeMax())
                .engagementLevel(prefs.getEngagementLevel())
                .languages(prefs.getLanguages())
                .build();

        service.save(updateReq);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "latitude", latitude,
                "longitude", longitude,
                "updatedAt", LocalDateTime.now().toString()
        ));
    }

    /**
     * Get nearby groups (to be implemented with actual group matching)
     * GET /api/users/{userId}/nearby-groups?latitude=X&longitude=Y&radius=Z
     */
    @GetMapping("/{userId}/nearby-groups")
    public ResponseEntity<Map<String, Object>> getNearbyGroups(
            @PathVariable Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radius) {

        // This endpoint would integrate with group matching
        // For now, returning a sample response structure
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "latitude", latitude,
                "longitude", longitude,
                "radius", radius + " km",
                "groups", new Object[]{},
                "message", "Nearby groups feature - to be implemented with group matching service"
        ));
    }

    /**
     * Get groups by distance (to be implemented with actual group matching)
     * GET /api/users/groups-by-distance?latitude=X&longitude=Y&maxDistance=Z
     */
    @GetMapping("/groups-by-distance")
    public ResponseEntity<Map<String, Object>> getGroupsByDistance(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double maxDistance) {

        // This endpoint would integrate with group matching
        // For now, returning a sample response structure
        return ResponseEntity.ok(Map.of(
                "latitude", latitude,
                "longitude", longitude,
                "maxDistance", maxDistance + " km",
                "groups", new Object[]{},
                "message", "Distance-based groups feature - to be implemented with group matching service"
        ));
    }
}
