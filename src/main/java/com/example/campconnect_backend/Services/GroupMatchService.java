package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.GroupMatchDto;
import com.example.campconnect_backend.Entities.GroupMatch;
import com.example.campconnect_backend.Repositories.GroupMatchRepository;
import com.example.campconnect_backend.Repositories.UserParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMatchService {

    private final GroupMatchRepository groupMatchRepo;
    private final UserParticipantRepository upRepo;

    @Transactional
    public GroupMatchDto.Response create(GroupMatchDto.Request req) {
        GroupMatch gm = GroupMatch.builder()
                .name(req.getName())
                .description(req.getDescription())
                .sport(req.getSport())
                .maxParticipants(req.getMaxParticipants())
                .scheduledAt(req.getScheduledAt())
                .location(req.getLocation())
                .build();
        return toResponse(groupMatchRepo.save(gm));
    }

    @Transactional(readOnly = true)
    public List<GroupMatchDto.Response> getAll() {
        return groupMatchRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupMatchDto.Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<GroupMatchDto.Response> getBySport(String sport) {
        return groupMatchRepo.findBySport(sport).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public GroupMatchDto.Response update(Long id, GroupMatchDto.Request req) {
        GroupMatch gm = findOrThrow(id);
        if (req.getName()            != null) gm.setName(req.getName());
        if (req.getDescription()     != null) gm.setDescription(req.getDescription());
        if (req.getSport()           != null) gm.setSport(req.getSport());
        if (req.getLocation()        != null) gm.setLocation(req.getLocation());
        if (req.getScheduledAt()     != null) gm.setScheduledAt(req.getScheduledAt());
        if (req.getMaxParticipants() > 0)     gm.setMaxParticipants(req.getMaxParticipants());
        return toResponse(groupMatchRepo.save(gm));
    }

    @Transactional
    public void delete(Long id) {
        if (!groupMatchRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupMatch introuvable");
        groupMatchRepo.deleteById(id);
    }

    private GroupMatch findOrThrow(Long id) {
        return groupMatchRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupMatch introuvable"));
    }

    private GroupMatchDto.Response toResponse(GroupMatch gm) {
        long count = upRepo.countAcceptedByGroupMatchId(gm.getId());
        return GroupMatchDto.Response.builder()
                .id(gm.getId())
                .name(gm.getName())
                .description(gm.getDescription())
                .sport(gm.getSport())
                .maxParticipants(gm.getMaxParticipants())
                .scheduledAt(gm.getScheduledAt())
                .location(gm.getLocation())
                .createdAt(gm.getCreatedAt())
                .participantCount(count)
                .build();
    }
}