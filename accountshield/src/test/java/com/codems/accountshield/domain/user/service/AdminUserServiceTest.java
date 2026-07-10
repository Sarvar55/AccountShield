package com.codems.accountshield.domain.user.service;

import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.Role;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.mapper.UserMapper;
import com.codems.accountshield.domain.user.repository.UserRepository;
import com.codems.accountshield.domain.user.service.data.AdminUserTestMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void updateRoleChangesRole() {
        UUID id = UUID.randomUUID();
        User user = AdminUserTestMother.regularUser();
        UserResponse response = AdminUserTestMother.adminResponse(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = adminUserService.updateRole(id, AdminUserTestMother.adminRoleRequest());

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void updateStatusUnlocksAndResetsFailureState() {
        UUID id = UUID.randomUUID();
        User user = AdminUserTestMother.lockedUser();
        UserResponse response = AdminUserTestMother.unlockedUserResponse(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = adminUserService.updateStatus(id, AdminUserTestMother.unlockedStatusRequest());

        assertThat(user.getFailedLoginAttempts()).isZero();
        assertThat(user.isAccountLocked()).isFalse();
    }
}
