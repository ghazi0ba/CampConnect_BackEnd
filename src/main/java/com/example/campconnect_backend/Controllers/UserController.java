package com.example.campconnect_backend.Controllers;



import com.example.campconnect_backend.Dto.OrderDto;
import com.example.campconnect_backend.Dto.UserDto;
import com.example.campconnect_backend.Entities.User;
import com.example.campconnect_backend.Repositories.UserRepository;
import com.example.campconnect_backend.Services.UserService;
import com.example.campconnect_backend.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;



    //ADMIN: create user
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.Response> createUser(
            @Valid @RequestBody UserDto.CreateRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }


    // Admin: get all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto.Response>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // Admin: get any user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // Any authenticated user: get their own profile
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getMe(Authentication auth) {

        System.out.println("AUTH = " + auth);

        if (auth != null) {
            System.out.println("PRINCIPAL CLASS = " + auth.getPrincipal().getClass());
            System.out.println("PRINCIPAL = " + auth.getPrincipal());
        }

        return ResponseEntity.ok(userService.findByEmail(getEmail(auth)));
    }
    // Any authenticated user: update their own profile
    @PutMapping("/me")
    public ResponseEntity<UserDto.Response> updateMe(@Valid @RequestBody UserDto.UpdateRequest request,
                                                     Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(userService.update(userId, request));
    }

    // Any authenticated user: change their own password
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody UserDto.ChangePasswordRequest request,
                                                 Authentication auth) {
        userService.changePassword(getUserId(auth), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    // Admin: update any user's role
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.Response> updateRole(@PathVariable Long id,
                                                       @RequestParam String role) {
        return ResponseEntity.ok(userService.updateRole(id, role));
    }

    // Admin: delete any user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String getEmail(Authentication auth) {
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(getEmail(auth)).orElseThrow().getId();
    }
}
