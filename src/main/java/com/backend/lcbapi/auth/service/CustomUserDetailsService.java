package com.backend.lcbapi.auth.service;

import com.backend.lcbapi.auth.entity.UserEntity;
import com.backend.lcbapi.shared.exceptions.NotFoundException;
import com.backend.lcbapi.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String userId) {
        UUID id = UUID.fromString(userId);

        UserEntity user = userRepository.findByUserId(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

}
