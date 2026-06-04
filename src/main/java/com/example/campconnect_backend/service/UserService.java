package com.example.campconnect_backend.service;

import com.example.campconnect_backend.model.CampingSite;
import com.example.campconnect_backend.model.User;
import com.example.campconnect_backend.repository.CampingSiteRepository;
import com.example.campconnect_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CampingSiteRepository siteRepository;

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Set<CampingSite> getFavorites(Long userId) {
        return getById(userId).getFavorites();
    }

    @Transactional
    public void toggleFavorite(Long userId, Long siteId) {
        User user = getById(userId);
        CampingSite site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Camping site not found"));

        if (user.getFavorites().contains(site)) {
            user.getFavorites().remove(site);
        } else {
            user.getFavorites().add(site);
        }
        userRepository.save(user);
    }
}
