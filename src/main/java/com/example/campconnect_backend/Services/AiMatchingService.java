package com.example.campconnect_backend.Services;



import com.example.campconnect_backend.Dto.GroupMatchDto;
import com.example.campconnect_backend.Dto.MatchResultDto;
import com.example.campconnect_backend.Dto.UserPreferenceDto;
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
    private final GroupMatchRepository groupRepo;
    private final UserParticipantRepository upRepo;
    private final UserRepository userRepo;
    private final MatchResultRepository matchRepo;

    // ── Poids des critères (total = 1.0) ────────────────────────────────────
    private static final double W_SPORT = 0.35;
    private static final double W_LOCATION = 0.30;
    private static final double W_SKILL = 0.15;
    private static final double W_AVAILABILITY = 0.10;
    private static final double W_GROUP_SIZE = 0.10;

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

        double sportScore = computeSportScore(pref, gm);
        double locationScore = computeLocationScore(pref, gm);
        double skillScore = computeSkillScore(pref, gm);
        double availabilityScore = computeAvailabilityScore(pref, gm);
        double groupSizeScore = computeGroupSizeScore(pref, gm);

        double total = (sportScore * W_SPORT)
                + (locationScore * W_LOCATION)
                + (skillScore * W_SKILL)
                + (availabilityScore * W_AVAILABILITY)
                + (groupSizeScore * W_GROUP_SIZE);

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
                gm.getLatitude(), gm.getLongitude());
        double radius = pref.getRadiusKm() > 0 ? pref.getRadiusKm() : 50.0;
        if (dist <= radius) return 1.0 - (dist / radius) * 0.4; // 1.0 → 0.6
        if (dist <= radius * 2) return 0.3;
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
        if (hour < 12) return "morning";
        if (hour < 18) return "afternoon";
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
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private boolean isGroupFull(GroupMatch gm) {
        return upRepo.countAcceptedByGroupMatchId(gm.getId()) >= gm.getMaxParticipants();
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

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

    // ── AI Chat Method ──────────────────────────────────────────────────────

    public String chatWithAI(String userMessage) {
        // Simple AI responses based on keywords
        String message = userMessage.toLowerCase().trim();

        if (message.contains("hello") || message.contains("hi") || message.contains("bonjour")) {
            return "Hello! I'm your CampConnect AI assistant. I can help you find sports groups, answer questions about matching, or provide information about our platform. How can I assist you today?";
        }

        if (message.contains("match") || message.contains("group") || message.contains("find")) {
            return "I'd be happy to help you find the perfect sports group! Based on your preferences, I can suggest groups that match your sport interests, skill level, location, and availability. Would you like me to run a matching algorithm for you?";
        }

        if (message.contains("sport") || message.contains("football") || message.contains("tennis") || message.contains("basketball")) {
            return "We support various sports including football, tennis, basketball, and more! Each group has different skill levels and locations. Tell me your preferred sport and I'll help you find matching groups.";
        }

        if (message.contains("location") || message.contains("where") || message.contains("distance")) {
            return "Location is important for finding nearby sports groups! You can set your preferred radius in your profile preferences. The matching algorithm considers distance to give you the best local options.";
        }

        if (message.contains("skill") || message.contains("level") || message.contains("beginner") || message.contains("advanced")) {
            return "Skill level matching ensures you play with people at your level. We have beginner, intermediate, and advanced groups. The algorithm matches you with groups within 1 skill level difference for the best experience.";
        }

        if (message.contains("help") || message.contains("how") || message.contains("what")) {
            return "CampConnect helps you find sports groups that match your preferences. You can set your sport interests, location, skill level, and availability. Then our AI matching algorithm finds the best groups for you. You can also chat with group members and join matches!";
        }

        if (message.contains("thank") || message.contains("thanks")) {
            return "You're welcome! Happy to help you find your perfect sports match. Enjoy playing!";
        }

        // Default response
        return "I'm here to help you with sports group matching and CampConnect features. You can ask me about finding groups, sports, locations, skill levels, or any other questions about the platform!";
    }
    @Transactional(readOnly = true)
    public List<GroupMatchDto.Response> findNearbyGroups(Long userId) {

        UserPreference pref = prefRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User preferences not found"));

        if (pref.getLatitude() == null || pref.getLongitude() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User location not defined");
        }

        double userLat = pref.getLatitude();
        double userLng = pref.getLongitude();

        double maxDistanceKm = (pref.getRadiusKm() > 0)
                ? pref.getRadiusKm()
                : 20.0;

        List<GroupMatch> groups = groupRepo.findAll();

        return groups.stream()

                // 1. filtrer groupes valides
                .filter(g -> g.getLatitude() != null && g.getLongitude() != null)

                // 2. calcul distance
                .map(g -> {
                    double distance = haversineKm(
                            userLat,
                            userLng,
                            g.getLatitude(),
                            g.getLongitude()
                    );
                    return new AbstractMap.SimpleEntry<>(g, distance);
                })

                // 3. filtrer par distance
                .filter(entry -> entry.getValue() <= maxDistanceKm)

                // 4. TRI INTELLIGENT (AI simple)
                .sorted((e1, e2) -> {
                    double score1 = computeSmartScore(pref, e1.getKey(), e1.getValue());
                    double score2 = computeSmartScore(pref, e2.getKey(), e2.getValue());
                    return Double.compare(score2, score1); // DESC
                })

                // 5. LIMIT
                .limit(20)

                // 6. MAP DTO
                .map(entry -> {
                    GroupMatch g = entry.getKey();
                    double distance = entry.getValue();

                    return GroupMatchDto.Response.builder()
                            .id(g.getId())
                            .name(g.getName())
                            .description(g.getDescription())
                            .sport(g.getSport())
                            .maxParticipants(g.getMaxParticipants())
                            .scheduledAt(g.getScheduledAt())
                            .location(g.getLocation())
                            .createdAt(g.getCreatedAt())
                            .participantCount(upRepo.countAcceptedByGroupMatchId(g.getId()))
                            .build();
                })

                .collect(Collectors.toList());
    }
    private double computeSmartScore(UserPreference pref, GroupMatch group, double distance) {

        double score = 0;

        // 🔹 Distance (plus proche = meilleur)
        score += (1 / (1 + distance)) * 50;

        // 🔹 Sport match
        if (pref.getSports() != null && group.getSport() != null) {
            List<String> sports = Arrays.stream(pref.getSports().split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            if (sports.contains(group.getSport().toLowerCase())) {
                score += 30;
            }
        }

        // 🔹 Taille groupe
        if (group.getMaxParticipants() >= pref.getGroupSizeMin()
                && group.getMaxParticipants() <= pref.getGroupSizeMax()) {
            score += 20;
        }

        return score;
    }

    private double computeUserCompatibility(UserPreference me, UserPreference other, double distance) {

        double score = 0;

        double radius = me.getRadiusKm() > 0 ? me.getRadiusKm() : 20;

        // distance
        if (distance <= radius) {
            score += (1 - (distance / radius)) * 40;
        } else return 0;

        // sport match
        if (me.getSports() != null && other.getSports() != null) {
            List<String> mySports = Arrays.stream(me.getSports().split(","))
                    .map(String::trim).map(String::toLowerCase).toList();

            List<String> otherSports = Arrays.stream(other.getSports().split(","))
                    .map(String::trim).map(String::toLowerCase).toList();

            if (mySports.stream().anyMatch(otherSports::contains)) {
                score += 30;
            }
        }

        // skill
        if (me.getSkillLevel() != null && other.getSkillLevel() != null) {
            int diff = Math.abs(me.getSkillLevel().ordinal() - other.getSkillLevel().ordinal());

            if (diff == 0) score += 20;
            else if (diff == 1) score += 10;
        }

        // availability
        if (me.getAvailability() != null && other.getAvailability() != null) {
            if (other.getAvailability().toLowerCase().contains(me.getAvailability().toLowerCase())) {
                score += 10;
            }
        }

        return score;
    }
    @Transactional(readOnly = true)
    public List<UserPreferenceDto.Response> findMatchingUsers(Long userId) {

        UserPreference me = prefRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Preferences not found"));

        if (me.getLatitude() == null || me.getLongitude() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User location missing");
        }

        double radius = me.getRadiusKm() > 0 ? me.getRadiusKm() : 20;

        return prefRepo.findAll().stream()

                // ❌ exclure moi-même
                .filter(p -> !p.getUser().getId().equals(userId))

                // ❌ localisation obligatoire
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)

                // 🔥 calcul distance + score
                .map(p -> {

                    double dist = haversineKm(
                            me.getLatitude(),
                            me.getLongitude(),
                            p.getLatitude(),
                            p.getLongitude()
                    );

                    double score = computeUserCompatibility(me, p, dist);

                    return new AbstractMap.SimpleEntry<>(p, new double[]{dist, score});
                })

                // 🔥 filtre rayon
                .filter(e -> e.getValue()[0] <= radius)

                // 🔥 tri par score
                .sorted((a, b) -> Double.compare(b.getValue()[1], a.getValue()[1]))

                // 🔥 limit
                .limit(20)

                // 🔥 mapping DTO EXISTANT
                .map(e -> {
                    UserPreference p = e.getKey();
                    double dist = e.getValue()[0];
                    double score = e.getValue()[1];

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
                })

                .toList();
    }

}