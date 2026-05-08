package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.GroupMatchDto;
import com.example.campconnect_backend.Dto.UserPreferenceDto;
import com.example.campconnect_backend.Entities.GroupMatch;
import com.example.campconnect_backend.Services.AiMatchingService;
import com.example.campconnect_backend.Services.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/userpreferences")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserPreferenceController {

    private final UserPreferenceService service;
    private final AiMatchingService matchingService ;


    @PostMapping("/{userId}/preferences")
    public ResponseEntity<UserPreferenceDto.Response> savePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferenceDto.Request req) {
        req.setUserId(userId);
        UserPreferenceDto.Response response = service.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/match-users")
    public ResponseEntity<List<UserPreferenceDto.Response>> matchUsers(
            @RequestParam Long userId) {

        return ResponseEntity.ok(matchingService.findMatchingUsers(userId));
    }

    @GetMapping("/{userId}/preferences")
    public ResponseEntity<UserPreferenceDto.Response> getPreferences(@PathVariable Long userId) {
        UserPreferenceDto.Response response = service.getByUser(userId);
        return ResponseEntity.ok(response);
    }


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


        UserPreferenceDto.Response prefs = service.getByUser(userId);


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

    @GetMapping("/{userId}/nearby-groups")
    public ResponseEntity<List<GroupMatchDto.Response>> getNearbyGroups(
            @PathVariable Long userId) {

        List<GroupMatchDto.Response> groups = matchingService.findNearbyGroups(userId);

        return ResponseEntity.ok(groups);
    }


    @GetMapping("/groups-by-distance")
    public  ResponseEntity<List<GroupMatchDto.Response>> getGroupsByDistance(
            @RequestParam Long userId) {

        List<GroupMatchDto.Response> groups = matchingService.findNearbyGroups(userId);

        return ResponseEntity.ok(groups);
    }
}
