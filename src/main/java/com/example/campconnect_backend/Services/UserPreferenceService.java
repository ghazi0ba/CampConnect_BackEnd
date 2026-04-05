package com.example.campconnect_backend.Services;



import com.example.campconnect_backend.Dto.UserPreferenceDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository prefRepo;
    private final UserRepository           userRepo;

    @Transactional
    public UserPreferenceDto.Response save(UserPreferenceDto.Request req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable"));

        UserPreference pref = prefRepo.findByUserId(req.getUserId())
                .orElse(UserPreference.builder().user(user).build());

        pref.setSports(req.getSports());
        pref.setSkillLevel(req.getSkillLevel());
        pref.setAgeMin(req.getAgeMin());
        pref.setAgeMax(req.getAgeMax());
        pref.setLatitude(req.getLatitude());
        pref.setLongitude(req.getLongitude());
        pref.setCity(req.getCity());
        pref.setRadiusKm(req.getRadiusKm() > 0 ? req.getRadiusKm() : 50.0);
        pref.setAvailability(req.getAvailability());
        pref.setGroupSizeMin(req.getGroupSizeMin());
        pref.setGroupSizeMax(req.getGroupSizeMax());
        pref.setEngagementLevel(req.getEngagementLevel());
        pref.setLanguages(req.getLanguages());

        return toResponse(prefRepo.save(pref));
    }

    @Transactional(readOnly = true)
    public UserPreferenceDto.Response getByUser(Long userId) {
        UserPreference pref = prefRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préférences introuvables"));
        return toResponse(pref);
    }

    private UserPreferenceDto.Response toResponse(UserPreference p) {
        return UserPreferenceDto.Response.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .sports(p.getSports())
                .skillLevel(p.getSkillLevel())
                .ageMin(p.getAgeMin())
                .ageMax(p.getAgeMax())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .city(p.getCity())
                .radiusKm(p.getRadiusKm())
                .availability(p.getAvailability())
                .groupSizeMin(p.getGroupSizeMin())
                .groupSizeMax(p.getGroupSizeMax())
                .engagementLevel(p.getEngagementLevel())
                .languages(p.getLanguages())
                .build();
    }
}
