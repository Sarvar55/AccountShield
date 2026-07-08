package com.codems.accountshield.domain.user.service;

import com.codems.accountshield.common.exceptions.types.ResourceNotFoundException;
import com.codems.accountshield.domain.user.dto.ProfileUpdateRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.mapper.UserMapper;
import com.codems.accountshield.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getProfile(String email) {
        return userMapper.toResponse(currentUser(email));
    }

    @Transactional
    public UserResponse updateProfile(String currentEmail, ProfileUpdateRequest request) {
        log.debug("Profile update requested for {}", currentEmail);
        User user = currentUser(currentEmail);

        user.setName(request.name().trim());
        user.setBio(request.bio().trim());
        log.info("Profile updated for {}", currentEmail);
        return userMapper.toResponse(user);
    }


    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

}
