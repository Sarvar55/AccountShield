package com.codems.accountshield.domain.user.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.codems.accountshield.common.security.properties.AdminProperties;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdminProperties adminProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = adminProperties.getEmail().trim();
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User admin = User.builder()
                .name(adminProperties.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(adminProperties.getPassword()))
                .role(Role.ADMIN)
                .emailVerified(true)
                .build();

        userRepository.save(admin);
    }
}
