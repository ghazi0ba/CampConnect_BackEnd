package com.example.campconnect_backend.config;

import com.example.campconnect_backend.model.User;
import com.example.campconnect_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword("password");
            user.setRole("USER");
            user.setPhone("0000000000");
            userRepository.save(user);
            System.out.println("Seeded default user with id=1");
        }
    }
}
