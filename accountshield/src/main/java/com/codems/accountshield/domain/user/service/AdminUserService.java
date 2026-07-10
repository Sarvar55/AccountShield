package com.codems.accountshield.domain.user.service;

import com.codems.accountshield.common.exceptions.types.ResourceNotFoundException;
import com.codems.accountshield.domain.user.dto.AdminUserRoleRequest;
import com.codems.accountshield.domain.user.dto.AdminUserStatusRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.mapper.UserMapper;
import com.codems.accountshield.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateStatus(UUID id, AdminUserStatusRequest request) {
        User user = findUser(id);
        user.setAccountLocked(request.accountLocked());
        user.setEmailVerified(request.emailVerified());
        if (!request.accountLocked()) {
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
        } else if (user.getLockedUntil() == null) {
            user.setLockedUntil(LocalDateTime.now().plusYears(100));
        }
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateRole(UUID id, AdminUserRoleRequest request) {
        User user = findUser(id);
        user.setRole(request.role());
        return userMapper.toResponse(user);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
