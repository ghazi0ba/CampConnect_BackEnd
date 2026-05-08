package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.UserDto;
import com.example.campconnect_backend.Entities.User;
import com.example.campconnect_backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    @Transactional
    public UserDto.Response create(UserDto.Request req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword()) // à hasher avec BCrypt en production
                .avatarUrl(req.getAvatarUrl())
                .build();

        return toResponse(userRepo.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDto.Response> getAll() {
        return userRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto.Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public UserDto.Response update(Long id, UserDto.UpdateRequest req) {
        User user = findOrThrow(id);
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getAvatarUrl() != null) user.setAvatarUrl(req.getAvatarUrl());
        return toResponse(userRepo.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable");
        userRepo.deleteById(id);
    }

    private User findOrThrow(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable"));
    }

    private UserDto.Response toResponse(User u) {
        return UserDto.Response.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .avatarUrl(u.getAvatarUrl())
                .build();
    }
}