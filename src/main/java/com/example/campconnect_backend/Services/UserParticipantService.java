package com.example.campconnect_backend.Services;
import com.example.campconnect_backend.Dto.UserParticipantDto;
import com.example.campconnect_backend.Entities.*;
import com.example.campconnect_backend.Repositories.GroupMatchRepository;
import com.example.campconnect_backend.Repositories.UserParticipantRepository;
import com.example.campconnect_backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class UserParticipantService {
 
    private final UserParticipantRepository upRepo;
    private final UserRepository userRepo;
    private final GroupMatchRepository groupMatchRepo;
 
  
 
    @Transactional
    public UserParticipantDto.Response join(UserParticipantDto.Request req) {
        if (upRepo.existsByUserIdAndGroupMatchId(req.getUserId(), req.getGroupMatchId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already in this GroupMatch");
        }
 
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
 
        GroupMatch gm = groupMatchRepo.findById(req.getGroupMatchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupMatch not found"));
 
        long accepted = upRepo.countAcceptedByGroupMatchId(gm.getId());
        if (accepted >= gm.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GroupMatch is full");
        }
 
        UserParticipant up = UserParticipant.builder()
                .id(new UserParticipantId(user.getId(), gm.getId()))
                .user(user)
                .groupMatch(gm)
                .role(req.getRole() != null ? req.getRole() : ParticipantRole.MEMBER)
                .status(req.getRole() == ParticipantRole.ADMIN ? ParticipantStatus.ACCEPTED : ParticipantStatus.PENDING)
                .score(0)
                .build();
 
        return toResponse(upRepo.save(up));
    }
 
   

 
    @Transactional(readOnly = true)
    public List<UserParticipantDto.Response> getByGroupMatch(Long groupMatchId) {
        return upRepo.findByGroupMatchId(groupMatchId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
 

 
    @Transactional(readOnly = true)
    public List<UserParticipantDto.Response> getByUser(Long userId) {
        return upRepo.findByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UserParticipantDto.Response> getAll() {
        return upRepo.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }



    @Transactional
    public UserParticipantDto.Response update(Long userId, Long groupMatchId, UserParticipantDto.UpdateRequest req) {
        UserParticipant up = upRepo.findById(new UserParticipantId(userId, groupMatchId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found"));

        if (req.getRole() != null)   up.setRole(req.getRole());
        if (req.getStatus() != null) up.setStatus(req.getStatus());
        up.setScore(req.getScore());

        return toResponse(upRepo.save(up));
    }
 

 
    @Transactional
    public void leave(Long userId, Long groupMatchId) {
        UserParticipantId id = new UserParticipantId(userId, groupMatchId);
        if (!upRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found");
        }
        upRepo.deleteById(id);
    }
 

 
    @Transactional(readOnly = true)
    public List<UserParticipantDto.Response> getLeaderboard() {
        return upRepo.findLeaderboard()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private UserParticipantDto.Response toResponse(UserParticipant up) {
        return UserParticipantDto.Response.builder()
                .userId(up.getUser().getId())
                .username(up.getUser().getUsername())
                .avatarUrl(up.getUser().getAvatarUrl())
                .groupMatchId(up.getGroupMatch().getId())
                .groupMatchName(up.getGroupMatch().getName())
                .role(up.getRole())
                .status(up.getStatus())
                .joinedAt(up.getJoinedAt())
                .score(up.getScore())
                .build();
    }
}
