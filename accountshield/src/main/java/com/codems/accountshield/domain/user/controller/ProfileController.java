package com.codems.accountshield.domain.user.controller;

import com.codems.accountshield.common.constants.ApplicationConstants;
import com.codems.accountshield.common.util.ApplicationUtility;
import com.codems.accountshield.domain.base.BaseResponse;
import com.codems.accountshield.domain.user.dto.ProfileUpdateRequest;
import com.codems.accountshield.domain.user.dto.UserResponse;
import com.codems.accountshield.domain.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<UserResponse>> getProfile() {
        var loggedInUser = ApplicationUtility.getLoggedInUser();
        return ResponseEntity.ok(BaseResponse.success(profileService.getProfile(loggedInUser.get().getEmail())));
    }

    @PutMapping(version = ApplicationConstants.API_VERSION)
    public ResponseEntity<BaseResponse<UserResponse>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        var loggedInUser = ApplicationUtility.getLoggedInUser();
        return ResponseEntity.ok(BaseResponse.success(profileService.updateProfile(loggedInUser.get().getEmail(), request)));
    }
}
