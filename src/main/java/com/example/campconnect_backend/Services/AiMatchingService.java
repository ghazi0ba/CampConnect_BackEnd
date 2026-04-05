package com.example.campconnect_backend.Services;



import com.example.campconnect_backend.Dto.MatchResultDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiMatchingService {

    private final UserPreferenceRepository prefRepo;
    private final GroupMatchRepository     groupRepo;
    private final UserParticipantRepository upRepo;
    private final UserRepository           userRepo;
    private final MatchResultRepository    matchRepo;

    // ── Poids des critères (total = 1.0) ────────────────────────────────────
    private static final double W_SPORT        = 0.35;
    private static final double W_LOCATION     = 0.30;
    private static final double W_SKILL        = 0.15;
    private static final double W_AVAILABILITY = 0.10;
    private static final double W_GROUP_SIZE   = 0.10;

    // ── Calculer les matches pour un user ───────────────────────────────────

    @Transactional
    public List<MatchResultDto.Response> computeMatches(Long userId) {
        UserPreference pref = prefRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Préférences utilisateur introuvables"));

        List<GroupMatch> groups = groupRepo.findAll();

        // Supprimer anciens résultats
        matchRepo.deleteByUserId(userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable"));

        List<MatchResult> results = groups.stream()
                .filter(gm -> !isGroupFull(gm))
                .map(gm -> buildMatchResult(user, pref, gm))
                .sorted(Comparator.comparingDouble(MatchResult::getCompatibilityScore).reversed())
                .limit(20)
                .collect(Collectors.toList());

        matchRepo.saveAll(results);
        return results.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Construire un MatchResult pour un groupe ─────────────────────────────

    private MatchResult buildMatchResult(User user, UserPreference pref, GroupMatch gm) {

        double sportScore        = computeSportScore(pref, gm);
        double locationScore     = computeLocationScore(pref, gm);
        double skillScore        = computeSkillScore(pref, gm);
        double availabilityScore = computeAvailabilityScore(pref, gm);
        double groupSizeScore    = computeGroupSizeScore(pref, gm);

        double total = (sportScore        * W_SPORT)
                + (locationScore     * W_LOCATION)
                + (skillScore        * W_SKILL)
                + (availabilityScore * W_AVAILABILITY)
                + (groupSizeScore    * W_GROUP_SIZE);

        double distanceKm = computeDistance(pref, gm);

        return MatchResult.builder()
                .user(user)
                .matchedGroup(gm)
                .compatibilityScore(round(total * 100))
                .sportScore(round(sportScore * 100))
                .locationScore(round(locationScore * 100))
                .skillScore(round(skillScore * 100))
                .availabilityScore(round(availabilityScore * 100))
                .groupSizeScore(round(groupSizeScore * 100))
                .distanceKm(round(distanceKm))
                .build();
    }

    // ── Critère 1 : Sport (35%) ──────────────────────────────────────────────
    // Score = 1 si le sport du groupe est dans les préférences, 0 sinon

    private double computeSportScore(UserPreference pref, GroupMatch gm) {
        if (pref.getSports() == null || gm.getSport() == null) return 0.5;
        List<String> userSports = Arrays.stream(pref.getSports().split(","))
                .map(String::trim).map(String::toLowerCase).toList();
        return userSports.contains(gm.getSport().toLowerCase()) ? 1.0 : 0.0;
    }

    // ── Critère 2 : Localisation (30%) ──────────────────────────────────────
    // Score décroît avec la distance selon rayon préféré

    private double computeLocationScore(UserPreference pref, GroupMatch gm) {
        if (pref.getLatitude() == null || gm.getLatitude() == null) return 0.5;
        double dist = haversineKm(pref.getLatitude(), pref.getLongitude(),
                gm.getLatitude(),  gm.getLongitude());
        double radius = pref.getRadiusKm() > 0 ? pref.getRadiusKm() : 50.0;
        if (dist <= radius)       return 1.0 - (dist / radius) * 0.4; // 1.0 → 0.6
        if (dist <= radius * 2)   return 0.3;
        return 0.0;
    }

    // ── Critère 3 : Niveau (15%) ─────────────────────────────────────────────

    private double computeSkillScore(UserPreference pref, GroupMatch gm) {
        if (pref.getSkillLevel() == null || gm.getSkillLevel() == null) return 0.5;
        int diff = Math.abs(pref.getSkillLevel().ordinal() - gm.getSkillLevel().ordinal());
        return switch (diff) {
            case 0 -> 1.0;
            case 1 -> 0.5;
            default -> 0.1;
        };
    }

    // ── Critère 4 : Disponibilité (10%) ──────────────────────────────────────

    private double computeAvailabilityScore(UserPreference pref, GroupMatch gm) {
        if (pref.getAvailability() == null || gm.getScheduledAt() == null) return 0.5;
        String slot = getTimeSlot(gm.getScheduledAt().getHour(),
                gm.getScheduledAt().getDayOfWeek().getValue());
        return pref.getAvailability().toLowerCase().contains(slot) ? 1.0 : 0.3;
    }

    private String getTimeSlot(int hour, int dayOfWeek) {
        if (dayOfWeek >= 6) return "weekend";
        if (hour < 12)      return "morning";
        if (hour < 18)      return "afternoon";
        return "evening";
    }

    // ── Critère 5 : Taille de groupe (10%) ───────────────────────────────────

    private double computeGroupSizeScore(UserPreference pref, GroupMatch gm) {
        int size = gm.getMaxParticipants();
        if (size >= pref.getGroupSizeMin() && size <= pref.getGroupSizeMax()) return 1.0;
        int diff = Math.min(Math.abs(size - pref.getGroupSizeMin()),
                Math.abs(size - pref.getGroupSizeMax()));
        return Math.max(0, 1.0 - diff * 0.15);
    }

    // ── Distance GPS (Haversine) ──────────────────────────────────────────────

    private double computeDistance(UserPreference pref, GroupMatch gm) {
        if (pref.getLatitude() == null || gm.getLatitude() == null) return -1;
        return haversineKm(pref.getLatitude(), pref.getLongitude(),
                gm.getLatitude(), gm.getLongitude());
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private boolean isGroupFull(GroupMatch gm) {
        return upRepo.countAcceptedByGroupMatchId(gm.getId()) >= gm.getMaxParticipants();
    }

    private double round(double v) { return Math.round(v * 10.0) / 10.0; }

    // ── Mapper ───────────────────────────────────────────────────────────────

    private MatchResultDto.Response toResponse(MatchResult mr) {
        GroupMatch gm = mr.getMatchedGroup();
        String label = mr.getCompatibilityScore() >= 75 ? "Excellent"
                : mr.getCompatibilityScore() >= 50 ? "Bon" : "Moyen";

        return MatchResultDto.Response.builder()
                .groupMatchId(gm.getId())
                .groupName(gm.getName())
                .sport(gm.getSport())
                .location(gm.getLocation())
                .groupLatitude(gm.getLatitude())
                .groupLongitude(gm.getLongitude())
                .maxParticipants(gm.getMaxParticipants())
                .currentParticipants(upRepo.countAcceptedByGroupMatchId(gm.getId()))
                .compatibilityScore(mr.getCompatibilityScore())
                .sportScore(mr.getSportScore())
                .locationScore(mr.getLocationScore())
                .skillScore(mr.getSkillScore())
                .availabilityScore(mr.getAvailabilityScore())
                .groupSizeScore(mr.getGroupSizeScore())
                .distanceKm(mr.getDistanceKm())
                .scheduledAt(gm.getScheduledAt())
                .matchLabel(label)
                .build();
    }
}