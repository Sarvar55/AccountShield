package com.codems.accountshield.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.codems.accountshield.domain.user.service.data.ProfileTestMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.entity.User;
import com.codems.accountshield.domain.user.mapper.UserMapper;
import com.codems.accountshield.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void updateProfileChangesNameAndBio() {
        User user = ProfileTestMother.userWithProfile();
        UserResponse response = ProfileTestMother.updatedResponse();

        when(userRepository.findByEmail(ProfileTestMother.EMAIL)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = profileService.updateProfile(ProfileTestMother.EMAIL, ProfileTestMother.updateRequest());

        assertThat(result.name()).isEqualTo(ProfileTestMother.NEW_NAME);
        assertThat(result.bio()).isEqualTo(ProfileTestMother.NEW_BIO);
        assertThat(user.getName()).isEqualTo(ProfileTestMother.NEW_NAME);
        assertThat(user.getBio()).isEqualTo(ProfileTestMother.NEW_BIO);
    }

}
