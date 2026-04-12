package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.*;
import com.example.campconnect_backend.Entities.Role;
import com.example.campconnect_backend.Entities.User;
import com.example.campconnect_backend.Repositories.UserRepository;
import com.example.campconnect_backend.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .email(userDetails.getUsername())
                        .build()
        );
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        // check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // create user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        //  encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // set role (default USER)
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        userRepository.save(user);

        // generate token directly after register
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .message("User registered successfully")
                        .build()
        );
    }
}