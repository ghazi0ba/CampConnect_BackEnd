package com.example.campconnect_backend.Services;



import com.example.campconnect_backend.Dto.UserDto;
import com.example.campconnect_backend.Entities.Role;
import com.example.campconnect_backend.Entities.User;
import com.example.campconnect_backend.exception.BadRequestException;
import com.example.campconnect_backend.exception.ResourceNotFoundException;
import com.example.campconnect_backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //create User only by ADMIN
    public UserDto.Response create(UserDto.CreateRequest request) {



        // 1. check email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        // 2. create user entity
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());


        user.setPassword(passwordEncoder.encode(request.getPassword()));



        // 5. save
        User saved = userRepository.save(user);

        return toResponse(saved);
    }

    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserDto.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public UserDto.Response findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return toResponse(user);
    }

    public UserDto.Response update(Long id, UserDto.UpdateRequest request) {
        User user = getOrThrow(id);

        if (!user.getEmail().equals(request.getEmail()) && userRepository.findByEmail(request.getEmail()) != null) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        return toResponse(userRepository.save(user));
    }

    public void changePassword(Long id, UserDto.ChangePasswordRequest request) {
        User user = getOrThrow(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserDto.Response updateRole(Long id, String role) {
        Role newRole;
        try {
            newRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role '" + role + "'. Valid values: USER, ADMIN");
        }
        User user = getOrThrow(id);
        user.setRole(newRole);
        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.delete(getOrThrow(id));
    }

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public UserDto.Response toResponse(User user) {
        UserDto.Response dto = new UserDto.Response();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }
}